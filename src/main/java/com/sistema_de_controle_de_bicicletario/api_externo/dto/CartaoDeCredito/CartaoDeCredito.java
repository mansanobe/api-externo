package com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartaoDeCredito {

    @JsonProperty("id")
    private long id;

    @JsonProperty("nomeTitular")
    private String nomeTitular;

    @JsonProperty("numero")
    private String numero;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validade")
    private LocalDate validade;

    @JsonProperty("cvv")
    private String cvv;
}
