package com.sistema_de_controle_de_bicicletario.api_externo.dto.Email;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailRequest {

    @JsonProperty("email")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "E-mail inválido")
    private String email;
    @JsonProperty("assunto")
    private String assunto;
    @JsonProperty("mensagem")
    private String mensagem;
}
