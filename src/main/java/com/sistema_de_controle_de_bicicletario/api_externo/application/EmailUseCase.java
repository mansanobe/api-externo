package com.sistema_de_controle_de_bicicletario.api_externo.application;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.EmailServiceInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailJPAEntity;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

import java.io.UnsupportedEncodingException;

@AllArgsConstructor
public class EmailUseCase {
    EmailServiceInterface servico;

    public Email enviarEmail(Email emailBase) throws MessagingException, UnsupportedEncodingException {
        return servico.enviarEmail(servico.constroiEmail(emailBase)) ? servico.salvarEmail(emailBase)  : null;
    }
}
