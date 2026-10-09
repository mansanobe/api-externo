package com.sistema_de_controle_de_bicicletario.api_externo.exception;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import lombok.Getter;

@Getter
public class ErroDeCobrancaException extends RuntimeException {
    private final transient Cobranca cobranca;
    public ErroDeCobrancaException(Cobranca cobrancaRecusada) {
        this.cobranca = cobrancaRecusada;
    }

}
