package com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class NovoCartaoDeCredito {

    @NotBlank(message = "Nome do titular não pode ser vazio")
    @JsonProperty("nomeTitular")
    private String nomeTitular;

    @NotBlank(message = "Número do cartão é obrigatório")
    @CreditCardNumber(message = "Número de cartão de crédito inválido.")
    @JsonProperty("numero")
    private String numero;


    @NotNull(message = "Data de validade é obrigatória")
    @Future(message = "Data de validade inválida")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validade")
    private LocalDate validade;

    @NotBlank(message = "CVV é obrigatório")
    @Pattern(regexp = "^\\d{3,4}$", message = "O CVV deve conter 3 ou 4 números.")
    @JsonProperty("cvv")
    private String cvv;
}
