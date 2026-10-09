package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.NovoCartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoInvalidoException;
import com.sistema_de_controle_de_bicicletario.api_externo.service.CartaoDeCreditoService;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CartaoDeCreditoControllerTest {

    @Mock
    CartaoDeCreditoService service;

    @InjectMocks
    CartaoDeCreditoController cartaoDeCreditoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidarCartaoDeCredito() throws StripeException {
        when(service.validarCartaoDeCredito("numeroCartao")).thenReturn(true);

        ResponseEntity<Void> result = cartaoDeCreditoController.validarCartaoDeCredito(new NovoCartaoDeCredito("nomeTitular", "numeroCartao", LocalDate.of(2025, Month.JUNE, 30), "cvv"));

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testValidarCartaoDeCreditoInvalido() throws StripeException {
        NovoCartaoDeCredito cartaoInvalido = new NovoCartaoDeCredito(
                "nomeTitular",
                "numeroCartaoInvalido",
                LocalDate.of(2025, Month.JUNE, 30),
                "cvv"
        );

        when(service.validarCartaoDeCredito("numeroCartaoInvalido")).thenReturn(false);

        CartaoInvalidoException exception = assertThrows(
                CartaoInvalidoException.class,
                () -> cartaoDeCreditoController.validarCartaoDeCredito(cartaoInvalido)
        );

        assertNotNull(exception);
        assertEquals("Motivo genérico", exception.getMotivoRecusa());
        assertEquals("numeroCartaoInvalido", exception.getNumero());
    }
}

