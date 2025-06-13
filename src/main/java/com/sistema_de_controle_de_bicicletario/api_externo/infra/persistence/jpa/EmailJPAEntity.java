package com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email")
@NoArgsConstructor
@Getter
public class EmailJPAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String email;
    private String assunto;
    private String mensagem;

    public EmailJPAEntity(String email, String mensagem, String assunto) {
        this.email = email;
        this.mensagem = mensagem;
        this.assunto = assunto;
    }

}
