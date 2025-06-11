package com.sistema_de_controle_de_bicicletario.api_externo.infra;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.dto.EmailRequest;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping
public class EmailController {
    private final EmailService emailService = new EmailService();
    @PostMapping("/enviarEmail")
    public ResponseEntity<Email> enviarEmail(@RequestBody @Valid EmailRequest emailRequest){
        try {
            emailService.enviarEmail(new Email(
                    emailRequest.getEmail(),
                    emailRequest.getAssunto(),
                    emailRequest.getMensagem()
            ));
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }catch (Throwable t) {
            return ResponseEntity.internalServerError().build();
        }

        return null;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> excecaoDeValidacao(MethodArgumentNotValidException ex){
        String mensagem = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(422).body(mensagem);
    }
}
