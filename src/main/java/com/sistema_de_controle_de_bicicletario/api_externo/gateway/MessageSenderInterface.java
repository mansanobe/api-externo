package com.sistema_de_controle_de_bicicletario.api_externo.gateway;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;

public interface MessageSenderInterface {
    Email enviarEmail(Email email);
}
