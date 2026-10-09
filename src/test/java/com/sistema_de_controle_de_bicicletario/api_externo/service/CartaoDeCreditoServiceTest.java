package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoDeCreditoNaoExisteException;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoInvalidoException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class CartaoDeCreditoServiceTest {

    CartaoDeCreditoService cartaoDeCreditoService;

    @BeforeEach
    void setUp() {
        cartaoDeCreditoService = new CartaoDeCreditoService("http://url.ms.aluguel");
    }
    @Test
    void testValidarCartaoDeCredito() throws StripeException {
        String numeroCartaoValido = "4242424242424242";
        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("requires_capture");
            when(paymentIntentMock.cancel()).thenReturn(paymentIntentMock);
            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);


            boolean result = cartaoDeCreditoService.validarCartaoDeCredito(numeroCartaoValido);


            assertTrue(result);
        }
    }

    @Test
    void testValidarCartaoDeCreditoComNumeroInexistente() {
        String numeroCartaoInexistente = "1234567890123456";

        CartaoDeCreditoNaoExisteException exception = assertThrows(
                CartaoDeCreditoNaoExisteException.class,
                () -> cartaoDeCreditoService.validarCartaoDeCredito(numeroCartaoInexistente)
        );

        assertEquals(numeroCartaoInexistente, exception.getNumero());
    }

    @Test
    void testValidarCartaoDeCreditoComStripeException() {
        String numeroCartaoValido = "4242424242424242";

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            StripeException stripeException = Mockito.mock(StripeException.class);
            com.stripe.model.StripeError stripeError = Mockito.mock(com.stripe.model.StripeError.class);

            when(stripeException.getStripeError()).thenReturn(stripeError);
            when(stripeError.getDeclineCode()).thenReturn("card_declined");

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(stripeException);

            CartaoInvalidoException exception = assertThrows(
                    CartaoInvalidoException.class,
                    () -> cartaoDeCreditoService.validarCartaoDeCredito(numeroCartaoValido)
            );

            assertEquals("Motivo desconhecido", exception.getMotivoRecusa());
            assertEquals(numeroCartaoValido, exception.getNumero());
        }
    }

    @Test
    void testValidarCartaoDeCreditoComStatusDiferenteDeRequiresCapture() throws StripeException {
        String numeroCartaoValido = "4242424242424242";
        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("succeeded");
            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            boolean result = cartaoDeCreditoService.validarCartaoDeCredito(numeroCartaoValido);

            assertFalse(result);
        }
    }

    @Test
    void testRealizarCobrancaComSucesso() throws StripeException {

        String numeroCartaoValido = "4242424242424242";
        long valor = 100L;

        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);
        PaymentIntent pagamentoCapturadoMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("requires_capture");
            when(paymentIntentMock.capture()).thenReturn(pagamentoCapturadoMock);
            when(pagamentoCapturadoMock.getStatus()).thenReturn("succeeded");

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            boolean result = cartaoDeCreditoService.realizarCobranca(numeroCartaoValido, valor);

            assertTrue(result);
        }
    }

    @Test
    void testRealizarCobrancaComPaymentIntentNaoPreparado() {

        String numeroCartaoValido = "4242424242424242";
        long valor = 100L;

        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("failed");

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            boolean result = cartaoDeCreditoService.realizarCobranca(numeroCartaoValido, valor);

            assertFalse(result);
        }
    }

    @Test
    void testRealizarCobrancaComFalhaAoCapturar() throws StripeException {

        String numeroCartaoValido = "4242424242424242";
        long valor = 100L;

        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);
        PaymentIntent pagamentoCapturadoMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("requires_capture");
            when(paymentIntentMock.capture()).thenReturn(pagamentoCapturadoMock);
            when(pagamentoCapturadoMock.getStatus()).thenReturn("failed");

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);

            boolean result = cartaoDeCreditoService.realizarCobranca(numeroCartaoValido, valor);

            assertFalse(result);
        }
    }

    @Test
    void testRealizarCobrancaComStripeException() {

        String numeroCartaoValido = "4242424242424242";
        long valor = 100L;

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            StripeException stripeException = Mockito.mock(StripeException.class);
            com.stripe.model.StripeError stripeError = Mockito.mock(com.stripe.model.StripeError.class);

            when(stripeException.getStripeError()).thenReturn(stripeError);
            when(stripeError.getDeclineCode()).thenReturn("card_declined");

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(stripeException);

            boolean result = cartaoDeCreditoService.realizarCobranca(numeroCartaoValido, valor);

            assertFalse(result);
        }
    }

    @Test
    void testRealizarCobrancaComCartaoInexistente() {
        // Arrange
        String numeroCartaoInexistente = "1234567890123456";
        long valor = 100L;

        // Act & Assert
        assertThrows(CartaoDeCreditoNaoExisteException.class, () -> cartaoDeCreditoService.realizarCobranca(numeroCartaoInexistente, valor));
    }

    @Test
    void testRealizarCobrancaComExceptionNoCapture() throws StripeException {

        String numeroCartaoValido = "4242424242424242";
        long valor = 100L;

        PaymentIntent paymentIntentMock = Mockito.mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentStatic = mockStatic(PaymentIntent.class)) {
            when(paymentIntentMock.getStatus()).thenReturn("requires_capture");
            when(paymentIntentMock.capture()).thenThrow(new StripeException("Erro ao capturar", "request_id", "code", 400) {});

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(paymentIntentMock);


            boolean result = cartaoDeCreditoService.realizarCobranca(numeroCartaoValido, valor);


            assertFalse(result);
        }
    }

    @Test
    void testResgatarDadosCartaoDeCreditoPorCiclistaComSucesso() throws Exception {
        // Arrange
        long ciclistaId = 1L;
        String respostaJson = "{\"numero\":\"4242424242424242\",\"nomeTitular\":\"Teste\",\"validade\":\"2030-12-12\",\"cvv\":\"123\"}";

        HttpClient httpClientMock = Mockito.mock(HttpClient.class);
        HttpResponse httpResponseMock = Mockito.mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(respostaJson);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(httpClientMock);

            // Act
            CartaoDeCredito cartao = cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(ciclistaId);

            // Assert
            assertNotNull(cartao);
            assertEquals("4242424242424242", cartao.getNumero());
            assertEquals("Teste", cartao.getNomeTitular());
            assertEquals("2030-12-12", cartao.getValidade().toString());
        }
    }

    @Test
    void testResgatarDadosCartaoDeCreditoPorCiclistaComErroStatus() throws Exception {
        // Arrange
        long ciclistaId = 2L;
        HttpClient httpClientMock = Mockito.mock(HttpClient.class);
        HttpResponse httpResponseMock = Mockito.mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(404);
        when(httpResponseMock.body()).thenReturn("Not Found");
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(httpClientMock);

            // Act & Assert
            IOException ex = assertThrows(IOException.class, () -> cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(ciclistaId));
            assertTrue(ex.getMessage().contains("Erro ao recuperar dados do cartão de crédito"));
        }
    }

    @Test
    void testResgatarDadosCartaoDeCreditoPorCiclistaComExcecao() throws Exception {
        // Arrange
        long ciclistaId = 3L;
        HttpClient httpClientMock = Mockito.mock(HttpClient.class);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Falha de rede"));

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(httpClientMock);

            // Act & Assert
            IOException ex = assertThrows(IOException.class, () -> cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(ciclistaId));
            assertTrue(ex.getMessage().contains("Erro ao construir a requisição"));
        }
    }
}