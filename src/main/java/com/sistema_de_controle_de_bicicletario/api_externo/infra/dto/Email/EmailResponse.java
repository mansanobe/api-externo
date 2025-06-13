package com.sistema_de_controle_de_bicicletario.api_externo.infra.dto.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailResponse {
    private Integer id;
    private String email;
    private String assunto;
    private String mensagem;
}
