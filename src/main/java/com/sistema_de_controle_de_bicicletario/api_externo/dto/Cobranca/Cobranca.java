package com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Cobranca {

    @JsonProperty("id")
    private long id;

    @JsonProperty("status")
    private CobrancaEntity.StatusCobranca status;

    @JsonProperty("horaSolicitacao")
    private LocalDateTime horaSolicitacao;

    @JsonProperty("horaFinalizacao")
    private LocalDateTime horaFinalizacao;

    @JsonProperty("valor")
    private long valor;

    @JsonProperty("ciclista")
    private long ciclista;

    public Cobranca(CobrancaEntity cobrancaEntity){
        this.id = cobrancaEntity.getId();
        this.status = cobrancaEntity.getStatus();
        this.horaSolicitacao = cobrancaEntity.getHoraSolicitacao();
        this.horaFinalizacao = cobrancaEntity.getHoraFinalizacao();
        this.valor = cobrancaEntity.getValor();
        this.ciclista = cobrancaEntity.getCiclista();
    }

    public Cobranca(CobrancaEntity.StatusCobranca status, LocalDateTime horaSolicitacao, LocalDateTime horaFinalizacao, long valor, long ciclista) {
        this.status = status;
        this.horaSolicitacao = horaSolicitacao;
        this.horaFinalizacao = horaFinalizacao;
        this.valor = valor;
        this.ciclista = ciclista;
    }

}
