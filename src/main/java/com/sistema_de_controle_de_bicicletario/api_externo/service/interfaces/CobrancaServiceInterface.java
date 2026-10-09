package com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;

import java.io.IOException;
import java.util.List;

public interface CobrancaServiceInterface {
    CobrancaEntity filaCobranca(NovaCobranca novaCobranca);

    CobrancaEntity realizarCobranca(NovaCobranca novaCobranca) throws IOException, InterruptedException;

    List<CobrancaEntity> processaCobrancasEmFila();

    CobrancaEntity obterCobrancaPorId(Long id);
}
