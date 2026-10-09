package com.sistema_de_controle_de_bicicletario.api_externo.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("squid:S2696")
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void inicializar(){
        Stripe.apiKey = stripeApiKey;
    }

}
