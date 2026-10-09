package com.sistema_de_controle_de_bicicletario.api_externo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Erro {
    @JsonProperty("codigo")
    private String codigo;
    @JsonProperty("mensagem")
    private String mensagem;
}
