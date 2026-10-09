package com.sistema_de_controle_de_bicicletario.api_externo.exception;

import lombok.Getter;

@Getter
public class CartaoDeCreditoNaoExisteException extends RuntimeException {
    String numero;
    public CartaoDeCreditoNaoExisteException(String numero) {
        this.numero = numero;
    }

}
