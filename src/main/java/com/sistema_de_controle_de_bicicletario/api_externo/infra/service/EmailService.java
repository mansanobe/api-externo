package com.sistema_de_controle_de_bicicletario.api_externo.infra.service;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.gateway.MessageSenderInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.persistence.jpa.EmailRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class EmailService implements MessageSenderInterface {
    private final EmailEntity emailEntity = new EmailEntity();
    private final EmailRepository emailRepository = new EmailRepository();

    @Override
    public Email enviarEmail(Email email) {


        return email;
    }
}
