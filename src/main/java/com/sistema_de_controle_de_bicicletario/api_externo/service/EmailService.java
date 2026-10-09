package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Setter
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
        javaMailSender.send(email);

        return true;
    }

    public MimeMessage constroiEmail(NovoEmail emailBase) throws MessagingException, UnsupportedEncodingException {

        String remetenteEmail = ambiente.getProperty("spring.mail.properties.mail.smtp.from");
        String nomeEmail = ambiente.getProperty("mail.from.name", "Sistema de Controle de Bicicletário");
        String assuntoEmail = emailBase.getAssunto();
        String mensagemEmail = emailBase.getMensagem();
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        email.setTo(emailBase.getEmail());
        email.setSubject(assuntoEmail);
        email.setFrom(new InternetAddress(remetenteEmail, nomeEmail));
        email.setText(mensagemEmail, mensagemEmail.matches("<[a-z][\\s\\S]*>"));
        return mimeMessage;
    }
}
