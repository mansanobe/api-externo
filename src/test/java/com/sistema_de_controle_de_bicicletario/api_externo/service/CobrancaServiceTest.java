package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CobrancaNotFound;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.ErroDeCobrancaException;
import com.sistema_de_controle_de_bicicletario.api_externo.repository.CobrancaRepository;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CartaoDeCreditoInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("sonar:S108")
class CobrancaServiceTest {

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private CartaoDeCreditoInterface cartaoDeCreditoService;

    @InjectMocks
    private CobrancaService cobrancaService;

    private NovaCobranca novaCobrancaValida;

    private CobrancaEntity cobrancaEntitySalva;

    private CobrancaEntity cobrancaEntityMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dados de teste válidos
        novaCobrancaValida = new NovaCobranca(100L, 1L);
        cobrancaEntityMock = new CobrancaEntity(
                CobrancaEntity.StatusCobranca.PAGA,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                100L,
                1L
        );

        // Mock da entidade que será retornada pelo repository
        cobrancaEntitySalva = new CobrancaEntity();
        cobrancaEntitySalva.setId(1L);
        cobrancaEntitySalva.setStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        cobrancaEntitySalva.setHoraSolicitacao(LocalDateTime.now());
        cobrancaEntitySalva.setValor(100L);
        cobrancaEntitySalva.setCiclista(1L);

        // Mock do CartaoDeCredito retornado
        CartaoDeCredito cartaoMock = new CartaoDeCredito(1L, "Titular Teste", "4242424242424242", LocalDate.of(2030, 12, 31), "123");
        try {
            when(cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(anyLong())).thenReturn(cartaoMock);
        } catch (Exception ignored) {}
            //ignora
        try {
            java.lang.reflect.Field cartaoField = CobrancaService.class.getDeclaredField("cartaoDeCreditoService");
            cartaoField.setAccessible(true);
            cartaoField.set(cobrancaService, cartaoDeCreditoService);
        } catch (Exception ignored) {
            // ignora
        }
    }

    @Test
    void testFilaCobrancaComSucesso() {
        // Arrange
        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenReturn(cobrancaEntitySalva);

        // Act
        CobrancaEntity result = cobrancaService.filaCobranca(novaCobrancaValida);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, result.getStatus());
        assertEquals(100L, result.getValor());
        assertEquals(1L, result.getCiclista());
        assertNotNull(result.getHoraSolicitacao());

        // Verifica se o repository foi chamado
        verify(cobrancaRepository, times(1)).save(any(CobrancaEntity.class));
    }

    @Test
    void testRealizarCobrancaComSucesso() throws ErroDeCobrancaException, IOException, InterruptedException {

        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong())).thenReturn(true);
        when(cobrancaRepository.save(any(CobrancaEntity.class))).thenReturn(cobrancaEntityMock);

        CobrancaEntity result = cobrancaService.realizarCobranca(novaCobrancaValida);

        assertNotNull(result);
        assertEquals(CobrancaEntity.StatusCobranca.PAGA, result.getStatus());
        assertEquals(100L, result.getValor());
        assertEquals(1L, result.getCiclista());

        verify(cartaoDeCreditoService, times(1)).realizarCobranca("4242424242424242", 100L);
        verify(cobrancaRepository, times(1)).save(any(CobrancaEntity.class));
    }

    @Test
    void testFilaCobrancaVerificaDadosPassadosParaRepository() {
        // Arrange
        ArgumentCaptor<CobrancaEntity> captor = ArgumentCaptor.forClass(CobrancaEntity.class);
        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenReturn(cobrancaEntitySalva);

        // Act
        cobrancaService.filaCobranca(novaCobrancaValida);

        // Assert
        verify(cobrancaRepository).save(captor.capture());
        CobrancaEntity cobrancaCapturada = captor.getValue();

        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, cobrancaCapturada.getStatus());
        assertEquals(100L, cobrancaCapturada.getValor());
        assertEquals(1L, cobrancaCapturada.getCiclista());
        assertNotNull(cobrancaCapturada.getHoraSolicitacao());
        assertNull(cobrancaCapturada.getHoraFinalizacao());
    }

    @Test
    void testRealizarCobrancaComFalhaNoCartao() {

        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong())).thenReturn(false);

        ErroDeCobrancaException exception = assertThrows(ErroDeCobrancaException.class, () -> cobrancaService.realizarCobranca(novaCobrancaValida));

        assertNotNull(exception.getCobranca());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, exception.getCobranca().getStatus());
        assertEquals(100L, exception.getCobranca().getValor());
        assertEquals(1L, exception.getCobranca().getCiclista());

        verify(cartaoDeCreditoService, times(1)).realizarCobranca("4242424242424242", 100L);
        verify(cobrancaRepository, never()).save(any(CobrancaEntity.class));
    }

    @Test
    void testFilaCobrancaVerificaStatusInicialPendente() {
        // Arrange
        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenReturn(cobrancaEntitySalva);

        // Act
        CobrancaEntity result = cobrancaService.filaCobranca(novaCobrancaValida);

        // Assert
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, result.getStatus());
        assertNull(result.getHoraFinalizacao());
    }

    @Test
    void testRealizarCobrancaComExceptionNoCartao() {

        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong()))
                .thenThrow(new RuntimeException("Erro no serviço de cartão"));

        assertThrows(RuntimeException.class, () -> cobrancaService.realizarCobranca(novaCobrancaValida));

        verify(cartaoDeCreditoService, times(1)).realizarCobranca("4242424242424242", 100L);
        verify(cobrancaRepository, never()).save(any(CobrancaEntity.class));
    }


    @Test
    void testFilaCobrancaVerificaHorarioSolicitacao() {
        // Arrange
        LocalDateTime antes = LocalDateTime.now();

        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenAnswer(invocation -> {
                    CobrancaEntity entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });

        // Act
        CobrancaEntity result = cobrancaService.filaCobranca(novaCobrancaValida);

        LocalDateTime depois = LocalDateTime.now();

        // Assert
        assertNotNull(result.getHoraSolicitacao());
        assertTrue(result.getHoraSolicitacao().isAfter(antes.minusSeconds(1)));
        assertTrue(result.getHoraSolicitacao().isBefore(depois.plusSeconds(1)));
    }

    @Test
    void testFilaCobrancaVerificaChamadaUnicaRepository() {
        // Arrange
        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenReturn(cobrancaEntitySalva);

        // Act
        cobrancaService.filaCobranca(novaCobrancaValida);

        // Assert
        verify(cobrancaRepository, times(1)).save(any(CobrancaEntity.class));
        verifyNoMoreInteractions(cobrancaRepository);
    }

    @Test
    void testRealizarCobrancaVerificaStatusPendente() {

        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong())).thenReturn(false);

        ErroDeCobrancaException exception = assertThrows(ErroDeCobrancaException.class, () -> cobrancaService.realizarCobranca(novaCobrancaValida));

        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, exception.getCobranca().getStatus());
        assertNotNull(exception.getCobranca().getHoraSolicitacao());
        assertNotNull(exception.getCobranca().getHoraFinalizacao());

        verify(cobrancaRepository, never()).save(any(CobrancaEntity.class));
    }

    @Test
    void testProcessaCobrancasEmFilaNenhumaCobranca() {
        when(cobrancaRepository.findByStatus(CobrancaEntity.StatusCobranca.PENDENTE))
                .thenReturn(new ArrayList<>());

        List<CobrancaEntity> resultado = cobrancaService.processaCobrancasEmFila();

        assertTrue(resultado.isEmpty());
        verify(cobrancaRepository, times(1)).findByStatus(CobrancaEntity.StatusCobranca.PENDENTE);
    }

    @Test
    void testProcessaCobrancasEmFilaComSucesso() {
        CobrancaEntity pendente = new CobrancaEntity(
                CobrancaEntity.StatusCobranca.PENDENTE,
                LocalDateTime.now(),
                null,
                100L,
                1L
        );
        List<CobrancaEntity> pendentes = List.of(pendente);

        when(cobrancaRepository.findByStatus(CobrancaEntity.StatusCobranca.PENDENTE))
                .thenReturn(pendentes);
        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong()))
                .thenReturn(true);
        when(cobrancaRepository.save(any(CobrancaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<CobrancaEntity> resultado = cobrancaService.processaCobrancasEmFila();

        assertEquals(1, resultado.size());
        assertEquals(CobrancaEntity.StatusCobranca.PAGA, resultado.get(0).getStatus());
        assertNotNull(resultado.get(0).getHoraFinalizacao());
        verify(cobrancaRepository, times(1)).findByStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        verify(cartaoDeCreditoService, times(1)).realizarCobranca(anyString(), eq(100L));
        verify(cobrancaRepository, times(1)).save(any(CobrancaEntity.class));
    }

    @Test
    void testProcessaCobrancasEmFilaComFalha() {
        CobrancaEntity pendente = new CobrancaEntity(
                CobrancaEntity.StatusCobranca.PENDENTE,
                LocalDateTime.now(),
                null,
                100L,
                1L
        );
        List<CobrancaEntity> pendentes = List.of(pendente);

        when(cobrancaRepository.findByStatus(CobrancaEntity.StatusCobranca.PENDENTE))
                .thenReturn(pendentes);
        when(cartaoDeCreditoService.realizarCobranca(anyString(), anyLong()))
                .thenReturn(false);

        List<CobrancaEntity> resultado = cobrancaService.processaCobrancasEmFila();

        assertEquals(1, resultado.size());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, resultado.get(0).getStatus());
        assertNull(resultado.get(0).getHoraFinalizacao());
        verify(cartaoDeCreditoService, times(1)).realizarCobranca(anyString(), eq(100L));
        verify(cobrancaRepository, never()).save(any(CobrancaEntity.class));
    }

    @Test
    void testObterCobrancaPorIdComSucesso() {
        CobrancaEntity cobranca = new CobrancaEntity();
        cobranca.setId(1L);
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));

        CobrancaEntity resultado = cobrancaService.obterCobrancaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(cobrancaRepository, times(1)).findById(1L);
    }

    @Test
    void testObterCobrancaPorIdNaoEncontrada() {
        when(cobrancaRepository.findById(99L)).thenReturn(Optional.empty());

        CobrancaNotFound exception = assertThrows(
                CobrancaNotFound.class,
                () -> cobrancaService.obterCobrancaPorId(99L)
        );
        assertTrue(exception.getMessage().contains("Cobrança não encontrada com o ID: 99"));
        verify(cobrancaRepository, times(1)).findById(99L);
    }
}