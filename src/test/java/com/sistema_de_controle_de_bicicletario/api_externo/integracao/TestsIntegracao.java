package com.sistema_de_controle_de_bicicletario.api_externo.integracao;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.repository.CobrancaRepository;
import com.sistema_de_controle_de_bicicletario.api_externo.service.BancoDeDadosService;
import com.sistema_de_controle_de_bicicletario.api_externo.service.CartaoDeCreditoService;
import com.sistema_de_controle_de_bicicletario.api_externo.service.CobrancaService;
import com.sistema_de_controle_de_bicicletario.api_externo.service.EmailService;
import com.stripe.model.PaymentIntent;
import jakarta.mail.internet.MimeMessage;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TestsIntegracao {

    static MockWebServer mockWebServer;

    @Autowired
    CartaoDeCreditoService cartaoDeCreditoService;

    @Autowired
    CobrancaService cobrancaService;

    @Autowired
    BancoDeDadosService bancoDeDadosService;

    @Autowired
    CobrancaRepository cobrancaRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        registry.add("url.aluguel", () -> mockWebServer.url("/").toString().replaceAll("/$", ""));
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void cleanDb() {
        cobrancaRepository.deleteAll();
    }

    @Test
    void testResgatarDadosCartaoDeCreditoPorCiclistaComSucesso() throws Exception {
        String respostaJson = "{\"numero\":\"4242424242424242\",\"nomeTitular\":\"Teste\",\"validade\":\"2030-12-12\",\"cvv\":\"123\"}";
        mockWebServer.enqueue(new MockResponse().setBody(respostaJson).setResponseCode(200));

        CartaoDeCredito cartao = cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(1L);

        assertNotNull(cartao);
        assertEquals("4242424242424242", cartao.getNumero());
        assertEquals("Teste", cartao.getNomeTitular());
        assertEquals("2030-12-12", cartao.getValidade().toString());
    }

    @Test
    void testResgatarDadosCartaoDeCreditoPorCiclistaComErroStatus() {
        mockWebServer.enqueue(new MockResponse().setBody("Not Found").setResponseCode(404));

        IOException ex = assertThrows(IOException.class, () -> cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(2L));
        assertTrue(ex.getMessage().contains("Erro ao recuperar dados do cartão de crédito"));
    }

    @Test
    void testFilaCobranca() {
        NovaCobranca nova = new NovaCobranca(100L, 1L);
        CobrancaEntity entity = cobrancaService.filaCobranca(nova);

        assertEquals(100L, entity.getValor());
        assertEquals(1L, entity.getCiclista());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, entity.getStatus());
    }

    @Test
    void testRealizarCobrancaComSucesso() throws Exception {
        String respostaJson = "{\"numero\":\"4242424242424242\",\"nomeTitular\":\"Teste\",\"validade\":\"2030-12-12\",\"cvv\":\"123\"}";
        mockWebServer.enqueue(new MockResponse().setBody(respostaJson).setResponseCode(200));

        NovaCobranca nova = new NovaCobranca(100L, 1L);
        CobrancaEntity entity = cobrancaService.realizarCobranca(nova);

        assertEquals(CobrancaEntity.StatusCobranca.PAGA, entity.getStatus());
    }

    @Test
    void testRealizarCobrancaComFalhaNoCartao() {
        String respostaJson = "{\"numero\":\"4000000000000002\",\"nomeTitular\":\"Teste\",\"validade\":\"2030-12-12\",\"cvv\":\"123\"}";
        mockWebServer.enqueue(new MockResponse().setBody(respostaJson).setResponseCode(200));

        NovaCobranca nova = new NovaCobranca(100L, 1L);

        assertThrows(Exception.class, () -> cobrancaService.realizarCobranca(nova));
    }

    @Test
    void testProcessaCobrancasEmFila() {
        // Adiciona cobrança pendente
        NovaCobranca nova = new NovaCobranca(100L, 1L);
        cobrancaService.filaCobranca(nova);

        // Mocka resposta do serviço de cartão
        String respostaJson = "{\"numero\":\"4242424242424242\",\"nomeTitular\":\"Teste\",\"validade\":\"2030-12-12\",\"cvv\":\"123\"}";
        mockWebServer.enqueue(new MockResponse().setBody(respostaJson).setResponseCode(200));

        List<CobrancaEntity> processadas = cobrancaService.processaCobrancasEmFila();

        assertFalse(processadas.isEmpty());
        assertEquals(CobrancaEntity.StatusCobranca.PAGA, processadas.get(0).getStatus());
    }

    @Test
    void testObterCobrancaPorId() {
        NovaCobranca nova = new NovaCobranca(100L, 1L);
        CobrancaEntity entity = cobrancaService.filaCobranca(nova);

        CobrancaEntity encontrada = cobrancaService.obterCobrancaPorId(entity.getId());
        assertNotNull(encontrada);
        assertEquals(entity.getId(), encontrada.getId());
    }

    @Test
    void testRestaurarBanco() {
        NovaCobranca nova = new NovaCobranca(100L, 1L);
        cobrancaService.filaCobranca(nova);

        assertTrue(cobrancaRepository.count() > 0);

        bancoDeDadosService.restaurarBanco();

        assertEquals(0, cobrancaRepository.count());
    }

    // Testes de e-mail

    @Test
    void testEnviarEmailComSucesso() throws Exception {
        JavaMailSender javaMailSender = mock(JavaMailSender.class);
        Environment environment = mock(Environment.class);
        EmailService emailService = new EmailService(javaMailSender, environment);

        NovoEmail novoEmail = new NovoEmail("email@test.com", "assunto", "mensagem");
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");

        MimeMessage email = emailService.constroiEmail(novoEmail);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        Boolean enviado = emailService.enviarEmail(email);

        assertTrue(enviado);
        verify(javaMailSender, times(1)).send(email);
    }

    @Test
    void testEnviarEmailComFalha() throws Exception {
        JavaMailSender javaMailSender = mock(JavaMailSender.class);
        Environment environment = mock(Environment.class);
        EmailService emailService = new EmailService(javaMailSender, environment);

        NovoEmail novoEmail = new NovoEmail("email@test.com", "assunto", "mensagem");
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");

        MimeMessage email = emailService.constroiEmail(novoEmail);
        doThrow(new RuntimeException("Falha no envio")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailService.enviarEmail(email));
    }

    @Test
    void testValidarCartaoDeCreditoValido() throws Exception {
        try (MockedStatic<PaymentIntent> mocked = mockStatic(PaymentIntent.class)) {
            PaymentIntent paymentIntent = mock(PaymentIntent.class);
            when(paymentIntent.getStatus()).thenReturn("requires_capture");
            mocked.when(() -> PaymentIntent.create(any(com.stripe.param.PaymentIntentCreateParams.class))).thenReturn(paymentIntent);

            boolean valido = cartaoDeCreditoService.validarCartaoDeCredito("4242424242424242");
            assertTrue(valido);
            verify(paymentIntent, times(1)).cancel();
        }
    }

    @Test
    void testValidarCartaoDeCreditoInvalido() throws Exception {
        try (MockedStatic<PaymentIntent> mocked = mockStatic(PaymentIntent.class)) {
            PaymentIntent paymentIntent = mock(PaymentIntent.class);
            when(paymentIntent.getStatus()).thenReturn("requires_payment_method");
            mocked.when(() -> PaymentIntent.create(any(com.stripe.param.PaymentIntentCreateParams.class))).thenReturn(paymentIntent);

            boolean valido = cartaoDeCreditoService.validarCartaoDeCredito("4000000000000002");
            assertFalse(valido);
        }
    }

    @Test
    void testValidarCartaoDeCreditoException() {
        // Testa cartão inexistente (não mapeado)
        assertThrows(Exception.class, () -> cartaoDeCreditoService.validarCartaoDeCredito("1234567890123456"));
    }
}