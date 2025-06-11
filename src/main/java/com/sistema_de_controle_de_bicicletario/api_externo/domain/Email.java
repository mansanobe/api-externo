package com.sistema_de_controle_de_bicicletario.api_externo.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Email {
    private String id;
    private String email;
    private String assunto;
    private String mensagem;

    public Email(String email, String Assunto, String mensagem) {
        this.email = email;
        this.assunto = Assunto;
        this.mensagem = mensagem;
    }
}
