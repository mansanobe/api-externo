package com.sistema_de_controle_de_bicicletario.api_externo.dto.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailResponse {
    private String email;
    private String assunto;
    private String mensagem;

    @Override
    public String toString() {
        return "{" +
                "\n   \"email\": \"" + email + '\"' +
                ", \n   \"assunto\": \"" + assunto + '\"' +
                ", \n   \"mensagem\": \"" + mensagem + '\"' + '\n' +
                '}';
    }
}
