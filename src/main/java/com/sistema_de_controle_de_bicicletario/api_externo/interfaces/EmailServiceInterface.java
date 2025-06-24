package com.sistema_de_controle_de_bicicletario.api_externo.interfaces;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

public interface EmailServiceInterface {
    Boolean enviarEmail(MimeMessage email);

    MimeMessage constroiEmail(EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException;
}
