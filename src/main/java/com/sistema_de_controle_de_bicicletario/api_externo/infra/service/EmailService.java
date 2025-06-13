package com.sistema_de_controle_de_bicicletario.api_externo.infra.service;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.EmailServiceInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailJPAEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailJPARepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService implements EmailServiceInterface {
    private final EmailJPARepository emailJPARepository;
    private final JavaMailSender javaMailSender;
    private final Environment ambiente;
    private final TemplateEngine htmlTemplateEngine;

    @Autowired
    public EmailService(EmailJPARepository emailJPARepository, JavaMailSender javaMailSender, Environment ambiente, TemplateEngine htmlTemplateEngine) {
        this.emailJPARepository = emailJPARepository;
        this.javaMailSender = javaMailSender;
        this.ambiente = ambiente;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    @Override
    public Boolean enviarEmail(MimeMessage email) {
        try{
            javaMailSender.send(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public MimeMessage constroiEmail(Email emailBase) throws MessagingException, UnsupportedEncodingException {
        String remetenteEmail = ambiente.getProperty("spring.mail.properties.mail.smtp.from");
        String nomeEmail = ambiente.getProperty("mail.from.name", "Sistema de Controle de Bicicletário");
        String assuntoEmail = emailBase.getAssunto();
        String mensagemEmail = emailBase.getMensagem();
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        final Context contexto = new Context(LocaleContextHolder.getLocale());

        email.setTo(emailBase.getEmail());
        email.setSubject(assuntoEmail);
        email.setFrom(new InternetAddress(remetenteEmail, nomeEmail));
        email.setText(mensagemEmail, false);;

        return mimeMessage;
    }

    public Email salvarEmail(Email emailBase){
        EmailJPAEntity emailJPAEntity = emailJPARepository.save(new EmailJPAEntity(
                emailBase.getEmail(),
                emailBase.getAssunto(),
                emailBase.getMensagem()));
        return new Email(
                emailJPAEntity.getId(),
                emailJPAEntity.getEmail(),
                emailJPAEntity.getAssunto(),
                emailJPAEntity.getMensagem()
        );
    }
}
