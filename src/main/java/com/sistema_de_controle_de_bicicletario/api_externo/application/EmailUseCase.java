package com.sistema_de_controle_de_bicicletario.api_externo.application;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.MessageSenderInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailUseCase {
    MessageSenderInterface mensageiro;

    public Email enviarEmail(Email email) {
        return mensageiro.enviarEmail(email);
    }
}
