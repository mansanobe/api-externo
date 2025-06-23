package com.sistema_de_controle_de_bicicletario.api_externo.infra.service.Email;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService implements EmailServiceInterface {
    private final JavaMailSender javaMailSender;
    private final Environment ambiente;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, Environment ambiente) {
        this.javaMailSender = javaMailSender;
        this.ambiente = ambiente;
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

        email.setTo(emailBase.getEmail());
        email.setSubject(assuntoEmail);
        email.setFrom(new InternetAddress(remetenteEmail, nomeEmail));
        email.setText(mensagemEmail, false);

        return mimeMessage;
    }
}
