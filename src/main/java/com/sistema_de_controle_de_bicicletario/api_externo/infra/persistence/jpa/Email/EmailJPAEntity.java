package com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.Email;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailJPAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String email;
    private String assunto;
    private String mensagem;

    public EmailJPAEntity(String email, String assunto, String mensagem) {
        this.email = email;
        this.mensagem = mensagem;
        this.assunto = assunto;
    }

}
