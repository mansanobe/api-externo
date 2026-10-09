package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.NovaCobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CobrancaNotFound;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CobrancaServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class CobrancaController {

    private final CobrancaServiceInterface cobrancaService;

    public CobrancaController(CobrancaServiceInterface cobrancaService) {
        this.cobrancaService = cobrancaService;
    }

    @PostMapping("/filaCobranca")
    public ResponseEntity<Cobranca> filaCobranca(@RequestBody @Valid NovaCobranca novaCobranca) {

        CobrancaEntity cobrancaEntity = cobrancaService.filaCobranca(novaCobranca);

        return ResponseEntity.ok(new Cobranca(cobrancaEntity));
    }

    @PostMapping("/cobranca")
    public ResponseEntity<Cobranca> cobranca(@RequestBody NovaCobranca novaCobranca) throws IOException, InterruptedException {
        Cobranca cobranca = new Cobranca(cobrancaService.realizarCobranca(novaCobranca));
        return ResponseEntity.ok().body(cobranca);
    }

    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<List<Cobranca>> processaCobrancasEmFila() {
        List<CobrancaEntity> cobrancasEmFila = cobrancaService.processaCobrancasEmFila();
        return ResponseEntity.ok().body(cobrancasEmFila.stream()
                .map(Cobranca::new)
                .toList());
    }

    @GetMapping("/cobranca/{id}")
    public ResponseEntity<Cobranca> obterCobrancaPorId(@PathVariable Long id){
        CobrancaEntity cobrancaEntity = cobrancaService.obterCobrancaPorId(id);
        if (cobrancaEntity == null) {
            throw new CobrancaNotFound("Não foi possível encontrar a cobrança com o ID: " + id);
        }
        return ResponseEntity.ok(new Cobranca(cobrancaEntity));
    }

}
