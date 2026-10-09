package com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import com.stripe.exception.StripeException;

import java.io.IOException;

public interface CartaoDeCreditoInterface {
    boolean validarCartaoDeCredito(String token) throws StripeException;

    boolean realizarCobranca(String token, long valor);

    CartaoDeCredito resgatarDadosCartaoDeCreditoPorCiclista(long ciclista) throws IOException, InterruptedException;

}
