package com.sistema_de_controle_de_bicicletario.api_externo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cobranca")
@NoArgsConstructor
@Getter
@Setter
public class CobrancaEntity {

    public enum StatusCobranca {
        PENDENTE,
        PAGA,
        FALHA,
        OCUPADA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusCobranca status;

    @Column(name = "horaSolicitacao")
    private LocalDateTime horaSolicitacao;

    @Column(name = "horaFinalizacao")
    private LocalDateTime horaFinalizacao;

    @Column(name = "valor")
    private long valor;

    @Column(name = "ciclista")
    private long ciclista;

    public CobrancaEntity(StatusCobranca status, LocalDateTime horaSolicitacao, LocalDateTime horaFinalizacao, long valor, long ciclista) {
        this.status = status;
        this.horaSolicitacao = horaSolicitacao;
        this.horaFinalizacao = horaFinalizacao;
        this.valor = valor;
        this.ciclista = ciclista;
    }

}
