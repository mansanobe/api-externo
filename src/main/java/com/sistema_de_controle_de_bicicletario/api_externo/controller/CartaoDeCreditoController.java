package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.NovoCartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoInvalidoException;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CartaoDeCreditoInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.service.CartaoDeCreditoService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CartaoDeCreditoController {

    CartaoDeCreditoInterface cartaoDeCreditoService;

    public CartaoDeCreditoController(CartaoDeCreditoInterface cartaoDeCreditoService) {
        this.cartaoDeCreditoService = cartaoDeCreditoService;
    }

    Logger logger = org.slf4j.LoggerFactory.getLogger(CartaoDeCreditoController.class);

    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<Void> validarCartaoDeCredito(@Valid @RequestBody NovoCartaoDeCredito cartaoDeCredito) throws StripeException {
        logger.info("Iniciando validação do cartão: {}", cartaoDeCredito.getNumero());
        if(cartaoDeCreditoService.validarCartaoDeCredito(cartaoDeCredito.getNumero())) {
            logger.info("Cartão validado com sucesso: {}", cartaoDeCredito.getNumero());
            return ResponseEntity.ok().build();
        }
        throw new CartaoInvalidoException("generic_decline", cartaoDeCredito.getNumero());
    }
}
