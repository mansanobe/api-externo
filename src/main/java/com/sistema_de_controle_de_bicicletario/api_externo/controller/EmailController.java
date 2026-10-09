package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.NovoEmail;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.EmailServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping
public class  EmailController {

    Logger logger = org.slf4j.LoggerFactory.getLogger(EmailController.class);

    private final EmailServiceInterface emailService;

    public EmailController(EmailServiceInterface emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<Email> enviarEmail(@RequestBody @Valid NovoEmail novoEmail) throws MessagingException, UnsupportedEncodingException {
        logger.info("Iniciando o envio de e-mail para: {}", novoEmail.getEmail());
        if (emailService.enviarEmail(emailService.constroiEmail(novoEmail))){
            logger.info("E-mail enviado com sucesso para: {}", novoEmail.getEmail());
            logger.info("Assunto: {}", novoEmail.getAssunto());
            logger.info("Mensagem: {}", novoEmail.getMensagem());
            return ResponseEntity.ok().body(new Email(novoEmail.getEmail(), novoEmail.getAssunto(), novoEmail.getMensagem()));
        }
        throw new MessagingException("Erro ao enviar e-mail");
    }

}
