package com.sistema_de_controle_de_bicicletario.api_externo.repository;

import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CobrancaRepository extends JpaRepository<CobrancaEntity, Long> {
    List<CobrancaEntity> findByStatus(CobrancaEntity.StatusCobranca statusCobranca);
}
