package com.sistema_de_controle_de_bicicletario.api_externo.dto.Email;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NovoEmail {

    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email no formato inválido")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "O assunto não pode ser vazio")
    @JsonProperty("assunto")
    private String assunto;

    @NotBlank(message = "A mensagem não pode ser vazia")
    @JsonProperty("mensagem")
    private String mensagem;
}
