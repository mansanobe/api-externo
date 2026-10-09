package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private Environment environment;

    @Mock
    EmailServiceInterface emailService;
    @InjectMocks
    EmailController emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnviarEmail() throws MessagingException, UnsupportedEncodingException {
        NovoEmail novoEmail = new NovoEmail("email@test.com", "assunto", "mensagem");
        MimeMessage mimeMessageMock = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessageMock);
        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");
        when(emailService.constroiEmail(any(NovoEmail.class))).thenReturn(mimeMessageMock);
        when(emailService.enviarEmail(mimeMessageMock)).thenReturn(true);

        ResponseEntity<Email> result = emailController.enviarEmail(novoEmail);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(novoEmail.getEmail(), result.getBody().getEmailUsuario());
        assertEquals(novoEmail.getAssunto(), result.getBody().getAssunto());
        assertEquals(novoEmail.getMensagem(), result.getBody().getMensagem());
    }

    @Test
    void testEnviarEmailComFalha() throws MessagingException, UnsupportedEncodingException {
        NovoEmail novoEmail = new NovoEmail("email@test.com", "assunto", "mensagem");
        MimeMessage mimeMessageMock = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessageMock);
        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");
        when(emailService.constroiEmail(any(NovoEmail.class))).thenReturn(mimeMessageMock);
        when(emailService.enviarEmail(mimeMessageMock)).thenReturn(false);

        MessagingException exception = assertThrows(
                MessagingException.class,
                () -> emailController.enviarEmail(novoEmail)
        );

        assertEquals("Erro ao enviar e-mail", exception.getMessage());
    }

}

