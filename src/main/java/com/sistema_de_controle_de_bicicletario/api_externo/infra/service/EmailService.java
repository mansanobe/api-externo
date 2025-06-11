package com.sistema_de_controle_de_bicicletario.api_externo.infra.service;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.MessageSenderInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailRepository;
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
public class EmailService implements MessageSenderInterface {
    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;
    private final Environment ambiente;
    private final TemplateEngine htmlTemplateEngine;

    @Autowired
    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender, Environment ambiente, TemplateEngine htmlTemplateEngine) {
        this.emailRepository = emailRepository;
        this.javaMailSender = javaMailSender;
        this.ambiente = ambiente;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    @Override
    public Email enviarEmail(Email email) {
        try{
            MimeMessage mensagemEmail = constroiEmail(email);
            javaMailSender.send(mensagemEmail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EmailEntity emailEntity = emailRepository.save(new EmailEntity(email.getEmail(), email.getMensagem(), email.getAssunto()));
        return new Email(emailEntity.getId(), emailEntity.getEmail(), emailEntity.getAssunto(), emailEntity.getMensagem());
    }

    private MimeMessage constroiEmail(Email emailBase) throws MessagingException, UnsupportedEncodingException {
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
        contexto.setVariable("email", emailBase.getEmail());
        contexto.setVariable("mensagem", mensagemEmail);

        String NOME_TEMPLATE = "emailTemplate";
        final String conteudoHTML = this.htmlTemplateEngine.process(NOME_TEMPLATE, contexto);

        email.setText(conteudoHTML, true);

        return mimeMessage;
    }
}
