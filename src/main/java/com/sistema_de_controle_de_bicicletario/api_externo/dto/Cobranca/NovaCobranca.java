package com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NovaCobranca {

    @NotNull(message = "O campo 'valor' não pode estar vazio.")
    @Min(value = 50, message = "O valor mínimo da cobrança é R$ 0,50.")//senao da erro no stripe
    @JsonProperty("valor")
    public long valor;

    @NotNull(message = "O campo 'ciclista' não pode estar vazio.")
    @Min(value = 1, message = "Ciclista inválido.")
    @JsonProperty("ciclista")
    public long ciclista;
}
