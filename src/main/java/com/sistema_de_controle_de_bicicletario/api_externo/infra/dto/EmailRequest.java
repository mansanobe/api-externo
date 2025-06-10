package com.sistema_de_controle_de_bicicletario.api_externo.infra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("assunto")
    private String assunto;
    @JsonProperty("mensagem")
    private String mensagem;
}
