package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Environment environment;

    @InjectMocks
    private EmailService emailService;

    private final NovoEmail email = new NovoEmail("bernmedman@gmail.com", "Assunto do Email", "Mensagem do Email");

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Instanciar o serviço manualmente para injetar a URL
        emailService = new EmailService(javaMailSender, environment);

        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");

        mimeMessage = new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    public void testEnviarEmail() {
        // Act
        boolean resultado = emailService.enviarEmail(mimeMessage);

        // Assert
        assertTrue(resultado);
        verify(javaMailSender, times(1)).send(mimeMessage);
    }


    @Test
    public void testConstroiEmailComEnderecoExistente() throws MessagingException, IOException, InterruptedException {
        // Arrange
        HttpClient httpClientMock = Mockito.mock(HttpClient.class);
        HttpResponse httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.body()).thenReturn("true");

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        try (MockedStatic<HttpClient> httpClientStatic = mockStatic(HttpClient.class)) {
            httpClientStatic.when(HttpClient::newHttpClient).thenReturn(httpClientMock);

            // Act
            MimeMessage result = emailService.constroiEmail(email);

            // Assert
            assertNotNull(result);
            assertEquals(mimeMessage, result);
        }
    }
}