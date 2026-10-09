package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Erro;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.*;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Erro>> errosDeFormatacao(MethodArgumentNotValidException ex) {
        List<FieldError> listaDeErros = ex.getFieldErrors();
        List<Erro> listaDeErrosTratados = new ArrayList<>();
        logger.error("Numero de erros de validação: {}", listaDeErros.size());
        for (FieldError erro : listaDeErros) {
            logger.error("Erro de validação no campo '{}': {}", erro.getField(), erro.getRejectedValue());
            listaDeErrosTratados.add(new Erro("422", "Erro de validação no campo '" + erro.getField() + "': " + erro.getDefaultMessage()));
        }
        return ResponseEntity.status(422).body(listaDeErrosTratados);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Erro> errosDeLeitura(HttpMessageNotReadableException ex) {
        logger.error("Dados inválidos: {}", ex.getCause().getMessage());
        if (ex.getRootCause() instanceof DateTimeException){
            logger.error("Erro de data inválida: {}", ex.getCause().getMessage());
            if (ex.getRootCause().getMessage().contains("Invalid value for MonthOfYear")){
                return ResponseEntity.status(422).body(new Erro("422", "Data não existe: Mês inválido"));
            }
            if (ex.getRootCause().getMessage().contains("Invalid value for DayOfMonth")){
                return ResponseEntity.status(422).body(new Erro("422", "Data não existe: Dia inválido"));
            }
            if (ex.getRootCause().getMessage().contains("Invalid value for Year")){
                return ResponseEntity.status(422).body(new Erro("422", "Data não existe: Ano inválido"));
            }
        }
        if (ex.getRootCause() instanceof DateTimeParseException) {
            logger.error("Data inválida: {}", ex.getCause().getMessage());
            return ResponseEntity.status(422).body(new Erro("422", "Data inválida"));
        }
        return ResponseEntity.status(422).body(new Erro("422", "Dados inválidos"));
    }

    @ExceptionHandler(CartaoDeCreditoNaoExisteException.class)
    public ResponseEntity<Erro> cartaoDeCreditoInexistente(CartaoDeCreditoNaoExisteException ex) {
        logger.error("Cartão de crédito inexistente: {}", ex.getNumero());
        return ResponseEntity.status(422).body(new Erro("422", "Cartão de crédito inexistente: " + ex.getNumero()));
    }

    @ExceptionHandler(CartaoInvalidoException.class)
    public ResponseEntity<Erro> errosDeValidacaoCartao(CartaoInvalidoException ex) {
        logger.error("Erro de validação de cartão: {}", ex.getMessage());
        return ResponseEntity.status(422).body(new Erro("422", "Erro de validação de cartão: " + ex.getMotivoRecusa()));
    }

    @ExceptionHandler(EmailNaoExisteException.class)
    public ResponseEntity<Erro> emailNaoExiste(EmailNaoExisteException ex) {
        logger.error("Endereço de e-mail não existe: {}", ex.getEmail());
        return ResponseEntity.status(404).body(new Erro("404", "Endereço de e-mail não existe: " + ex.getEmail()));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Erro> erroAoConstruirEmail(MessagingException ex) {
        logger.error("Erro ao construir o e-mail: {}", ex.getMessage());
        Erro erro = new Erro("500", "Erro no envio do e-mail");
        return ResponseEntity.status(500).body(erro);
    }

    //falta tratamento de erro pro cartao declinado

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Erro> erroAoEnviarEmail(MailSendException ignoredEx) {
        logger.error("Endereço de e-mail não existe");
        Erro erro = new Erro("500", "Erro no envio do e-mail");
        return ResponseEntity.status(500).body(erro);
    }

    @ExceptionHandler(ErroDeCobrancaException.class)
    public ResponseEntity<Cobranca> pagamentoRecusado(ErroDeCobrancaException ex) {
        logger.error("Erro ao realizar cobrança: {}", ex.getMessage());
        Cobranca cobranca = ex.getCobranca();
        return ResponseEntity.status(200).body(cobranca);
    }

    @ExceptionHandler(CobrancaNotFound.class)
    public ResponseEntity<Erro> cobrancaNotFound(CobrancaNotFound ex) {
        logger.error("Cobrança não encontrada: {}", ex.getMessage());
        return ResponseEntity.status(404).body(new Erro("404", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Erro> handleRuntimeException(RuntimeException ex) {
        logger.error("Erro interno do servidor: {}", ex.getMessage());
        Erro erro = new Erro("500", "Erro interno do servidor");
        return ResponseEntity.status(500).body(erro);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Erro> handleIOException(IOException ex) {
        logger.error("Erro de entrada/saída: {}", ex.getMessage());
        Erro erro = new Erro("500", "Erro Interno do servidor");
        return ResponseEntity.status(500).body(erro);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<Erro> handleInterruptedException(InterruptedException ex) {
        logger.error("Erro na comunicação com API de aluguel: {}", ex.getMessage());
        Erro erro = new Erro("500", "Erro Interno do servidor");
        return ResponseEntity.status(500).body(erro);
    }
}
