package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.EmailRequest;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Email.EmailResponse;
import com.sistema_de_controle_de_bicicletario.api_externo.interfaces.EmailServiceInterface;
import com.sistema_de_controle_de_bicicletario.api_externo.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class EmailController {
    private final EmailServiceInterface emailService;

    @Autowired
    public EmailController(JavaMailSender javaMailSender, Environment environment) {
        this.emailService = new EmailService(javaMailSender, environment);
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<String> enviarEmail(@RequestBody @Valid EmailRequest emailRequest){
        try {
            EmailResponse emailResponse = emailService.enviarEmail(emailService.constroiEmail(emailRequest)) ?  new EmailResponse(emailRequest.getEmail(), emailRequest.getAssunto(), emailRequest.getMensagem()) : null;
            assert emailResponse != null;
            return ResponseEntity.ok().body(emailResponse.toString());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> excecaoDeValidacao(MethodArgumentNotValidException ex){
        String mensagem = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(422).body(mensagem);
    }
}
