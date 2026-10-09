package com.sistema_de_controle_de_bicicletario.api_externo.exception;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartaoInvalidoExceptionTest {



    @ParameterizedTest
    @MethodSource("motivosCartao")
    void testMotivosDeRecusa(String motivo, String mensagemEsperada) {
        String numeroCartao = "4242424242424242";
        CartaoInvalidoException exception = new CartaoInvalidoException(motivo, numeroCartao);
        assertEquals(mensagemEsperada, exception.getMotivoRecusa());
        assertEquals(numeroCartao, exception.getNumero());
    }

    private static Stream<Arguments> motivosCartao() {
        return Stream.of(
                Arguments.of("generic_decline", "Motivo genérico"),
                Arguments.of("insufficient_funds", "Fundos insuficientes"),
                Arguments.of("lost_card", "Cartão perdido"),
                Arguments.of("stolen_card", "Cartão roubado"),
                Arguments.of("expired_card", "Cartão expirado"),
                Arguments.of("incorrect_cvc", "CVC incorreto"),
                Arguments.of("processing_error", "Erro de processamento"),
                Arguments.of("incorrect_number", "Número de cartão incorreto"),
                Arguments.of("card_velocity_exceeded", "Limite de transações excedido"),
                Arguments.of("motivo_inexistente", "Motivo desconhecido"),
                Arguments.of("", "Motivo desconhecido")
        );
    }
}