package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.repository.CobrancaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class BancoDeDadosServiceTest {
    @Mock
    CobrancaRepository cobrancaRepository;

    @InjectMocks
    BancoDeDadosService bancoDeDadosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRestaurarBancoDeletaRegistros() {
        when(cobrancaRepository.count()).thenReturn(5L);

        bancoDeDadosService.restaurarBanco();

        verify(cobrancaRepository, times(1)).count();
        verify(cobrancaRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(cobrancaRepository);
    }

    @Test
    void testRestaurarBancoComZeroRegistros() {
        when(cobrancaRepository.count()).thenReturn(0L);

        bancoDeDadosService.restaurarBanco();

        verify(cobrancaRepository, times(1)).count();
        verify(cobrancaRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(cobrancaRepository);
    }
}