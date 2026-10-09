package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.BancoDeDadosServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class BancoDeDadosControllerTest {
    @Mock
    BancoDeDadosServiceInterface bancoDeDadosService;

    @InjectMocks
    BancoDeDadosController bancoDeDadosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRestaurarBanco() {
        ResponseEntity<Void> result = bancoDeDadosController.restaurarBanco();
        verify(bancoDeDadosService, times(1)).restaurarBanco();
        assertEquals(200, result.getStatusCodeValue());
        assertNull(result.getBody());
    }
}