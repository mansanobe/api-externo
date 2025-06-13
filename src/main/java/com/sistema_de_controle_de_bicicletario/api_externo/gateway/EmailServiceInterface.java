package com.sistema_de_controle_de_bicicletario.api_externo.gateway;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailJPAEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

public interface EmailServiceInterface {
    Boolean enviarEmail(MimeMessage email);

    MimeMessage constroiEmail(Email email) throws MessagingException, UnsupportedEncodingException;

    Email salvarEmail(Email email);
}
