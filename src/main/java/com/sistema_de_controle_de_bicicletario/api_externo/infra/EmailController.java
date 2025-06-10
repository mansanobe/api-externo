package com.sistema_de_controle_de_bicicletario.api_externo.infra;

import com.sistema_de_controle_de_bicicletario.api_externo.domain.Email;
import com.sistema_de_controle_de_bicicletario.api_externo.infra.dto.EmailRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping
public class EmailController {
    @PostMapping("/enviarEmail")
    public ResponseEntity<Email> enviarEmail(@RequestBody EmailRequest emailRequest){

        return null;
    }
}
