package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.BancoDeDadosServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BancoDeDadosController {

    private final BancoDeDadosServiceInterface bancoDeDadosService;

    public BancoDeDadosController(BancoDeDadosServiceInterface bancoDeDadosService) {
        this.bancoDeDadosService = bancoDeDadosService;
    }

    @GetMapping("/restaurarBanco")
    public ResponseEntity<Void> restaurarBanco() {
        bancoDeDadosService.restaurarBanco();
        return ResponseEntity.ok().build();
    }

}
