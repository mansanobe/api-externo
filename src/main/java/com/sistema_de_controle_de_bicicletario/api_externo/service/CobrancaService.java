package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CobrancaNotFound;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.ErroDeCobrancaException;
import com.sistema_de_controle_de_bicicletario.api_externo.repository.CobrancaRepository;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CartaoDeCreditoInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CobrancaServiceInterface;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CobrancaService implements CobrancaServiceInterface {

    private final CobrancaRepository cobrancaRepository;

    public CobrancaService(CobrancaRepository cobrancaRepository, CartaoDeCreditoService cartaoDeCreditoService){
        this.cobrancaRepository = cobrancaRepository;
        this.cartaoDeCreditoService = cartaoDeCreditoService;
    }


    CartaoDeCreditoInterface cartaoDeCreditoService;

    Logger logger = org.slf4j.LoggerFactory.getLogger(CobrancaService.class);

    @Transactional
    public CobrancaEntity filaCobranca(NovaCobranca novaCobranca) {
        logger.info("Iniciando inclusão de nova cobranca na fila: {}", LocalDateTime.now());
        CobrancaEntity cobrancaEntity = new CobrancaEntity();
        cobrancaEntity.setStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        cobrancaEntity.setHoraSolicitacao(LocalDateTime.now());
        cobrancaEntity.setValor(novaCobranca.getValor());
        cobrancaEntity.setCiclista(novaCobranca.getCiclista());
        logger.info("Nova cobrança criada: {}", cobrancaEntity);
        return cobrancaRepository.save(cobrancaEntity);
    }

    public CobrancaEntity realizarCobranca(NovaCobranca novaCobranca) throws ErroDeCobrancaException, IOException, InterruptedException {
        LocalDateTime horaSolicitacao = LocalDateTime.now();
        logger.info("Iniciando o processo de cobrança as {} para o ciclista: {}", horaSolicitacao, novaCobranca.getCiclista());
        CartaoDeCredito cartaoDeCredito = cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(novaCobranca.getCiclista());
        if (!cartaoDeCreditoService.realizarCobranca(cartaoDeCredito.getNumero(), novaCobranca.getValor())){
            logger.error("Erro ao realizar cobrança para o ciclista: {}", novaCobranca.getCiclista());
            throw new ErroDeCobrancaException(new Cobranca(CobrancaEntity.StatusCobranca.PENDENTE,
                    horaSolicitacao,
                    LocalDateTime.now(), novaCobranca.getValor(), novaCobranca.getCiclista()));
        }
        LocalDateTime horaFinalizacao = LocalDateTime.now();
        logger.info("Cobrança finalizada as {} para o ciclista: {}", horaFinalizacao, novaCobranca.getCiclista());
        return cobrancaRepository.save(new CobrancaEntity(CobrancaEntity.StatusCobranca.PAGA, horaSolicitacao, horaFinalizacao, novaCobranca.getValor(), novaCobranca.getCiclista()));
    }

    public List<CobrancaEntity> processaCobrancasEmFila() {
        List<CobrancaEntity> cobrancasEmFila = cobrancaRepository.findByStatus(CobrancaEntity.StatusCobranca.PENDENTE);
        List<CobrancaEntity> cobrancas;
        if (cobrancasEmFila.isEmpty()) {
            logger.info("Nenhuma cobrança pendente encontrada.");
            return cobrancasEmFila;
        }
        cobrancas = cobrancasEmFila.parallelStream()
                .map(cobrancaPendente -> {
                    try {
                        CartaoDeCredito cartaoDeCredito = cartaoDeCreditoService.resgatarDadosCartaoDeCreditoPorCiclista(cobrancaPendente.getCiclista());

                        logger.info("Processando cobrança pendente: {}", cobrancaPendente);
                        if(!cartaoDeCreditoService.realizarCobranca(cartaoDeCredito.getNumero(), cobrancaPendente.getValor())) {
                            logger.error("Falha ao processar cobrança para o ciclista: {}", cobrancaPendente.getCiclista());
                            throw new ErroDeCobrancaException(new Cobranca(cobrancaPendente));
                        }
                        logger.info("Cobrança processada com sucesso para o ciclista: {}", cobrancaPendente.getCiclista());
                        cobrancaPendente.setStatus(CobrancaEntity.StatusCobranca.PAGA);
                        cobrancaPendente.setHoraFinalizacao(LocalDateTime.now());
                        logger.info("Cobrança processada com sucesso: {}", cobrancaPendente);
                        return cobrancaRepository.save(cobrancaPendente);
                    } catch (ErroDeCobrancaException | IOException e) {
                        logger.error("Erro ao processar cobrança: {}", e.getMessage());
                        return cobrancaPendente;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Thread interrompida ao processar cobrança: {}", e.getMessage());
                        return cobrancaPendente;
                    }
                }).toList();
        return cobrancas;
    }

    public CobrancaEntity obterCobrancaPorId(Long id) {
        return cobrancaRepository.findById(id)
                .orElseThrow(() -> new CobrancaNotFound("Cobrança não encontrada com o ID: " + id));
    }
}
