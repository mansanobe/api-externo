package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.sistema_de_controle_de_bicicletario.api_externo.repository.CobrancaRepository;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.BancoDeDadosServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class BancoDeDadosService implements BancoDeDadosServiceInterface {


    CobrancaRepository cobrancaRepository;

    public BancoDeDadosService(CobrancaRepository cobrancaRepository) {
        this.cobrancaRepository = cobrancaRepository;
    }

    Logger logger = Logger.getLogger(BancoDeDadosService.class.getName());

    @Transactional
    public void restaurarBanco() {
        if (Objects.nonNull(cobrancaRepository)) {
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info(() -> "Deletando " + cobrancaRepository.count() + " registros do banco de dados");
            }
            cobrancaRepository.deleteAll();
            logger.info("Banco de dados restaurado com sucesso");
        } else {
            logger.warning("cobrancaRepository está null. Operação abortada.");
        }
    }

}
