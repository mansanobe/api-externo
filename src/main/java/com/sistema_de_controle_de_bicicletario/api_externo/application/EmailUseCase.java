package com.sistema_de_controle_de_bicicletario.api_externo.application;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.MessageSenderInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.service.EmailService;

public class EmailUseCase {
    MessageSenderInterface mensageiro = new EmailService();

    public Email enviarEmail(Email email) {
        return mensageiro.enviarEmail(email);
    }
}
