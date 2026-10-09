package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CobrancaNotFound;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CobrancaServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CobrancaControllerTest {

    @Mock
    private CobrancaServiceInterface cobrancaService;

    @InjectMocks
    private CobrancaController cobrancaController;

    private NovaCobranca novaCobrancaValida;
    private CobrancaEntity cobrancaEntityMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        novaCobrancaValida = new NovaCobranca(100L, 1L);

        cobrancaEntityMock = new CobrancaEntity();
        cobrancaEntityMock.setId(1L);
        cobrancaEntityMock.setStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        cobrancaEntityMock.setHoraSolicitacao(LocalDateTime.now());
        cobrancaEntityMock.setValor(100L);
        cobrancaEntityMock.setCiclista(1L);
    }

    @Test
    void testFilaCobrancaComSucesso() {
        when(cobrancaService.filaCobranca(any(NovaCobranca.class)))
                .thenReturn(cobrancaEntityMock);

        ResponseEntity<Cobranca> result = cobrancaController.filaCobranca(novaCobrancaValida);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, result.getBody().getStatus());
        assertEquals(100L, result.getBody().getValor());
        assertEquals(1L, result.getBody().getCiclista());

        verify(cobrancaService, times(1)).filaCobranca(novaCobrancaValida);
    }

    @Test
    void testFilaCobrancaComValorMinimo() {
        NovaCobranca novaCobrancaMinima = new NovaCobranca(50L, 1L);
        when(cobrancaService.filaCobranca(any(NovaCobranca.class)))
                .thenReturn(cobrancaEntityMock);

        ResponseEntity<Cobranca> result = cobrancaController.filaCobranca(novaCobrancaMinima);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cobrancaService, times(1)).filaCobranca(novaCobrancaMinima);
    }

    @Test
    void testFilaCobrancaVerificaChamadaService() {
        when(cobrancaService.filaCobranca(any(NovaCobranca.class)))
                .thenReturn(cobrancaEntityMock);

        cobrancaController.filaCobranca(novaCobrancaValida);

        verify(cobrancaService, times(1)).filaCobranca(novaCobrancaValida);
        verifyNoMoreInteractions(cobrancaService);
    }

    @Test
    void testCobrancaComSucesso() throws Exception {

        when(cobrancaService.realizarCobranca(any(NovaCobranca.class)))
                .thenReturn(cobrancaEntityMock);


        ResponseEntity<Cobranca> result = cobrancaController.cobranca(novaCobrancaValida);


        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(cobrancaEntityMock.getValor(), result.getBody().getValor());
        assertEquals(cobrancaEntityMock.getCiclista(), result.getBody().getCiclista());
        assertEquals(cobrancaEntityMock.getStatus(), result.getBody().getStatus());
        assertEquals(cobrancaEntityMock.getHoraSolicitacao(), result.getBody().getHoraSolicitacao());
        assertNotNull(result.getBody());
        verify(cobrancaService, times(1)).realizarCobranca(novaCobrancaValida);
    }

    @Test
    void testProcessaCobrancasEmFilaSemPendentes() {
        when(cobrancaService.processaCobrancasEmFila()).thenReturn(List.of());

        ResponseEntity<List<Cobranca>> response = cobrancaController.processaCobrancasEmFila();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(cobrancaService, times(1)).processaCobrancasEmFila();
    }

    @Test
    void testProcessaCobrancasEmFilaComPendentes() {
        CobrancaEntity cobranca1 = new CobrancaEntity();
        cobranca1.setId(1L);
        cobranca1.setStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        cobranca1.setValor(100L);
        cobranca1.setCiclista(1L);

        CobrancaEntity cobranca2 = new CobrancaEntity();
        cobranca2.setId(2L);
        cobranca2.setStatus(CobrancaEntity.StatusCobranca.PAGA);
        cobranca2.setValor(200L);
        cobranca2.setCiclista(2L);

        when(cobrancaService.processaCobrancasEmFila()).thenReturn(List.of(cobranca1, cobranca2));

        ResponseEntity<List<Cobranca>> response = cobrancaController.processaCobrancasEmFila();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, response.getBody().get(0).getStatus());
        assertEquals(2L, response.getBody().get(1).getId());
        assertEquals(CobrancaEntity.StatusCobranca.PAGA, response.getBody().get(1).getStatus());
        verify(cobrancaService, times(1)).processaCobrancasEmFila();
    }

    @Test
    void testProcessaCobrancasEmFilaVerificaChamadaService() {
        when(cobrancaService.processaCobrancasEmFila()).thenReturn(List.of());

        cobrancaController.processaCobrancasEmFila();

        verify(cobrancaService, times(1)).processaCobrancasEmFila();
        verifyNoMoreInteractions(cobrancaService);
    }

    @Test
    void testObterCobrancaPorIdComSucesso() {
        when(cobrancaService.obterCobrancaPorId(1L)).thenReturn(cobrancaEntityMock);

        ResponseEntity<Cobranca> response = cobrancaController.obterCobrancaPorId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(cobrancaService, times(1)).obterCobrancaPorId(1L);
    }

    @Test
    void testObterCobrancaPorIdNaoEncontrada() {
        when(cobrancaService.obterCobrancaPorId(99L)).thenReturn(null);

        Exception exception = assertThrows(
                CobrancaNotFound.class,
                () -> cobrancaController.obterCobrancaPorId(99L)
        );
        assertTrue(exception.getMessage().contains("Não foi possível encontrar a cobrança com o ID: 99"));
        verify(cobrancaService, times(1)).obterCobrancaPorId(99L);
    }
}