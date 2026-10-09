package com.sistema_de_controle_de_bicicletario.api_externo.dto.Email;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Email {
    @JsonProperty("email")
    private String emailUsuario;
    @JsonProperty("assunto")
    private String assunto;
    @JsonProperty("mensagem")
    private String mensagem;
}