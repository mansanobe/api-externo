package com.sistema_de_controle_de_bicicletario.api_externo.exception;

import lombok.Getter;

@Getter
public class CartaoInvalidoException extends RuntimeException {
    private final String motivoRecusa;
    private final String numero;

    public CartaoInvalidoException(String codigoDeclinacao, String numero) {
        switch (codigoDeclinacao) {
            case "generic_decline":
                motivoRecusa = "Motivo genérico";
                break;
            case "insufficient_funds":
                motivoRecusa = "Fundos insuficientes";
                break;
            case "lost_card":
                motivoRecusa = "Cartão perdido";
                break;
            case "stolen_card":
                motivoRecusa = "Cartão roubado";
                break;
            case "expired_card":
                motivoRecusa = "Cartão expirado";
                break;
            case "incorrect_cvc":
                motivoRecusa = "CVC incorreto";
                break;
            case "processing_error":
                motivoRecusa = "Erro de processamento";
                break;
            case "incorrect_number":
                motivoRecusa = "Número de cartão incorreto";
                break;
            case "card_velocity_exceeded":
                motivoRecusa = "Limite de transações excedido";
                break;
            default:
                motivoRecusa = "Motivo desconhecido";
        }

        this.numero = numero;
    }
}
