package com.sistema_de_controle_de_bicicletario.api_externo.infra.Controller;

import com.sistema_de_controle_de_bicicletario.api_externo.application.EmailUseCase;
import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.dto.Email.EmailRequest;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.dto.Email.EmailResponse;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.service.Email.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class EmailController {
    private final EmailUseCase emailUseCase;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailUseCase = new EmailUseCase(emailService);
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<String> enviarEmail(@RequestBody @Valid EmailRequest emailRequest){
        try {
            Email emailBase = new Email(emailRequest.getEmail(), emailRequest.getAssunto(), emailRequest.getMensagem());
            EmailResponse emailResponse = emailUseCase.enviarEmail(emailBase) ?  new EmailResponse(emailBase.getEmail(), emailBase.getAssunto(), emailBase.getMensagem()) : null;
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
