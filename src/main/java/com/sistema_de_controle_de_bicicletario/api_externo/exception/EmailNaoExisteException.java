package com.sistema_de_controle_de_bicicletario.api_externo.exception;

import lombok.Getter;

@Getter
public class EmailNaoExisteException extends RuntimeException {
    public String email;
    public EmailNaoExisteException(String email) {
        this.email = email;
    }


}

