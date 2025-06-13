package com.sistema_de_controle_de_bicicletario.api_externo;


import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.Email.EmailJPAEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.Email.EmailJPARepository;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.service.Email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EmailServiceTest {
    @Mock
    private EmailJPARepository emailJPARepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Environment environment;

    @Mock
    private TemplateEngine htmlTemplateEngine;



    @InjectMocks
    private EmailService emailService;

    Email email = new Email("bernmedman@gmail.com", "Assunto do Email", "Mensagem do Email");

    MimeMessage mimeMessage;

    @BeforeEach
    void setUp() throws MessagingException {

        MockitoAnnotations.openMocks(this); // Necessário para @Mock e @InjectMocks


        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("bicicletarioemail@gmail.com");
        when(environment.getProperty("mail.from.name", "Sistema de Controle de Bicicletário")).thenReturn("Sistema de Controle de Bicicletário");


        mimeMessage  = new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }


    @Test
    public void testEnviarEmail() throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("bicicletarioemail@gmail.com");
        mimeMessageHelper.setTo(email.getEmail());
        mimeMessageHelper.setSubject(email.getAssunto());
        mimeMessageHelper.setText(email.getMensagem(), false);

        assertTrue(emailService.enviarEmail(mimeMessage));
    }

    @Test
    public void testConstroiEmail() throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("bicicletarioemail@gmail.com");
        mimeMessageHelper.setTo(email.getEmail());
        mimeMessageHelper.setSubject(email.getAssunto());
        mimeMessageHelper.setText(email.getMensagem(), false);

        assertEquals(mimeMessage, emailService.constroiEmail(email));

    }

    @Test
    public void testSalvarEmail() {
        Email emailComId = new Email(1, email.getEmail(), email.getAssunto(), email.getMensagem());
        EmailJPAEntity emailJPAEntity = new EmailJPAEntity(1, email.getEmail(), email.getAssunto(), email.getMensagem());
        when(emailJPARepository.save(any())).thenReturn(emailJPAEntity);
        Email emailSalvo = emailService.salvarEmail(email);
        assertEquals(emailComId.getEmail(), emailSalvo.getEmail());
        assertEquals(emailComId.getId(), emailSalvo.getId());
        assertEquals(emailComId.getAssunto(), emailSalvo.getAssunto());
        assertEquals(emailComId.getMensagem(), emailSalvo.getMensagem());
    }
}
