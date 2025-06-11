package com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, String> {
}
