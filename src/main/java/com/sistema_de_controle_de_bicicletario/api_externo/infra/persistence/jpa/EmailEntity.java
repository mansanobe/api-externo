package com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "email")
@NoArgsConstructor
@Getter
public class EmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String email;
    private String assunto;
    private String mensagem;

    public EmailEntity(String email, String mensagem, String assunto) {
        this.email = email;
        this.mensagem = mensagem;
        this.assunto = assunto;
    }
}
