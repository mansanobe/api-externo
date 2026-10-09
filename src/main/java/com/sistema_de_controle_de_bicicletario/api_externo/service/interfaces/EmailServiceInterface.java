package com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

public interface EmailServiceInterface {
    Boolean enviarEmail(MimeMessage email);

    MimeMessage constroiEmail(NovoEmail novoEmail) throws MessagingException, UnsupportedEncodingException;
}
