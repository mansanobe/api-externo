package com.sistema_de_controle_de_bicicletario.api_externo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.CartaoDeCredito.CartaoDeCredito;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoDeCreditoNaoExisteException;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.CartaoInvalidoException;
import com.sistema_de_controle_de_bicicletario.api_externo.service.interfaces.CartaoDeCreditoInterface;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Service
@SuppressWarnings("sonar:S2139")
public class CartaoDeCreditoService implements CartaoDeCreditoInterface {

    private final Logger logger = LoggerFactory.getLogger(CartaoDeCreditoService.class);

    String urlAluguel;

    @Autowired
    public CartaoDeCreditoService(@Value("${url.aluguel}") String urlAluguel) {
        this.urlAluguel = urlAluguel;
    }

    @Override
    public boolean validarCartaoDeCredito(String numero) throws StripeException {

        PaymentIntent paymentIntent = criaPaymentIntent(50L, numero);

        if ("requires_capture".equals(paymentIntent.getStatus())) {
            paymentIntent.cancel();
            logger.info("Cartão validado");
            return true;
        }

        return false;
    }

    public boolean realizarCobranca(String numero, long valor) {
        try {
            PaymentIntent paymentIntent = criaPaymentIntent(valor, numero);
            if (!paymentIntent.getStatus().equals("requires_capture")) {
                logger.error("PaymentIntent não preparado para cobranca: {}", paymentIntent.getStatus());
                logger.error("Erro as: {}", LocalDateTime.now());
                return false;
            }

            PaymentIntent pagamentoCapturado = paymentIntent.capture();

            if (!"succeeded".equals(pagamentoCapturado.getStatus())) {
                logger.error("Erro ao capturar o pagamento: {}", paymentIntent.getStatus());
                logger.error("Erro as: {}", LocalDateTime.now());
                return false;
            }
            logger.info("Cobrança realizada com sucesso para o cartão: {}", numero);
            return true;
        }catch (CartaoInvalidoException | StripeException e){
            return false;
        }

    }

    private String resgataToken(String numero) {
        Map<String, String> cartoes = new LinkedHashMap<>();
        cartoes.put("4242424242424242", "pm_card_visa");
        cartoes.put("4000000760000002", "pm_card_br");
        cartoes.put("4000000000000002", "pm_card_visa_chargeDeclined");
        cartoes.put("4000000000009995", "pm_card_visa_chargeDeclinedInsufficientFunds");
        cartoes.put("4000000000009987", "pm_card_visa_chargeDeclinedLostCard");
        cartoes.put("4000000000009979", "pm_card_visa_chargeDeclinedStolenCard");
        cartoes.put("4000000000000069", "pm_card_chargeDeclinedExpiredCard");
        cartoes.put("4000000000000127", "pm_card_chargeDeclinedIncorrectCvc");
        cartoes.put("4000000000000119", "pm_card_chargeDeclinedProcessingError");
        cartoes.put("4000000000006975", "pm_card_visa_chargeDeclinedVelocityLimitExceeded");
        if (!cartoes.containsKey(numero)) {
            logger.error("Número de cartão inválido: {}", numero);
            throw new CartaoDeCreditoNaoExisteException(numero);
        }
        return cartoes.get(numero);
    }

    private PaymentIntent criaPaymentIntent(long valor, String numero){
        String token = resgataToken(numero);
        if (token.isEmpty()) {
            logger.error("Número de cartão inválido: {}", numero);
            throw new CartaoDeCreditoNaoExisteException(numero);
        }
        String MOEDA = "brl";
        PaymentIntentCreateParams.AutomaticPaymentMethods automaticPaymentMethods =
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                        .build();

        //Cria parametros para a intenção de pagamento, setando a captura do pagamento como manual
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(valor)
                .setCurrency(MOEDA)
                .setPaymentMethod(token)
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                .setConfirm(true)
                .setAutomaticPaymentMethods(automaticPaymentMethods)
                .build();

        try {
            return PaymentIntent.create(params);
        } catch (StripeException e) {
            logger.error("Erro ao criar PaymentIntent para o cartão {} e valor {}: {}", numero, valor, e.getMessage(), e);//NOSONAR
            throw new CartaoInvalidoException(e.getStripeError().getDeclineCode(), numero); //NOSONAR
        }
    }

    public CartaoDeCredito resgatarDadosCartaoDeCreditoPorCiclista(long ciclista) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = urlAluguel + "/cartaoDeCredito/" + ciclista;
        objectMapper.registerModule(new JavaTimeModule());

        try{
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> respostaRequisicao = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (respostaRequisicao.statusCode() != 200) {
                logger.error("Erro ao recuperar dados do cartão de crédito para o ciclista: {}", ciclista);
                throw new IOException("Erro ao recuperar dados do cartão de crédito: " + respostaRequisicao.body());
            }
            return objectMapper.readValue(respostaRequisicao.body(), CartaoDeCredito.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Erro ao construir a requisição: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erro ao construir a requisição para resgatar dados do cartão de crédito para a url {}: {}", url, e.getMessage());
            throw new IOException("Erro ao construir a requisição: " + e.getMessage(), e);
        }
    }
}
