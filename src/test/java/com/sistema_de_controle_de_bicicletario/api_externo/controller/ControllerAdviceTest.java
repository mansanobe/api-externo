package com.sistema_de_controle_de_bicicletario.api_externo.controller;

import com.sistema_de_controle_de_bicicletario.api_externo.dto.Cobranca.Cobranca;
import com.sistema_de_controle_de_bicicletario.api_externo.dto.Erro;
import com.sistema_de_controle_de_bicicletario.api_externo.entity.CobrancaEntity;
import com.sistema_de_controle_de_bicicletario.api_externo.exception.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerAdviceTest {

    ControllerAdvice controllerAdvice = new ControllerAdvice();

    @Test
    void testErrosDeFormatacao() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("novoCartao", "numero", "123", false, null, null, "Número de cartão de crédito inválido.");
        FieldError fieldError2 = new FieldError("novoEmail", "assunto", "", false, null, null, "O assunto não pode ser vazio");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<List<Erro>> result = controllerAdvice.errosDeFormatacao(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        assertEquals("422", result.getBody().get(0).getCodigo());
        assertEquals("Erro de validação no campo 'numero': Número de cartão de crédito inválido.", result.getBody().get(0).getMensagem());
        assertEquals("Erro de validação no campo 'assunto': O assunto não pode ser vazio", result.getBody().get(1).getMensagem());
    }

    @ParameterizedTest
    @MethodSource("provideDateTimeExceptionCases")
    void testErrosDeLeituraComDataInvalida(String exceptionMessage, String mensagemEsperada) {
        DateTimeException cause = new DateTimeException(exceptionMessage);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("msg", cause);

        ResponseEntity<Erro> result = controllerAdvice.errosDeLeitura(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("422", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals(mensagemEsperada, result.getBody().getMensagem());
    }

    private static Stream<Arguments> provideDateTimeExceptionCases() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        "Invalid value for MonthOfYear (valid values 1 - 12): 13",
                        "Data não existe: Mês inválido"
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Invalid value for DayOfMonth (valid values 1 - 28/31): 32",
                        "Data não existe: Dia inválido"
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Invalid value for Year",
                        "Data não existe: Ano inválido"
                )
        );
    }

    @Test
    void testErrosDeLeituraComDateTimeParseException() {
        DateTimeParseException cause = new DateTimeParseException("Text could not be parsed", "invalid-date", 0);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("msg", cause);

        ResponseEntity<Erro> result = controllerAdvice.errosDeLeitura(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("422", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Data inválida", result.getBody().getMensagem());
    }

    @Test
    void testErrosDeLeituraGenerico() {
        RuntimeException cause = new RuntimeException("Generic error");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("msg", cause);

        ResponseEntity<Erro> result = controllerAdvice.errosDeLeitura(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("422", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Dados inválidos", result.getBody().getMensagem());
    }

    @Test
    void testCartaoDeCreditoInexistente() {
        CartaoDeCreditoNaoExisteException exception = new CartaoDeCreditoNaoExisteException("1234567890123456");

        ResponseEntity<Erro> result = controllerAdvice.cartaoDeCreditoInexistente(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("422", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Cartão de crédito inexistente: 1234567890123456", result.getBody().getMensagem());
    }

    @Test
    void testErrosDeValidacaoCartao() {
        CartaoInvalidoException exception = new CartaoInvalidoException("insufficient_funds", "1234567890123456");

        ResponseEntity<Erro> result = controllerAdvice.errosDeValidacaoCartao(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("422", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Erro de validação de cartão: Fundos insuficientes", result.getBody().getMensagem());
    }

    @Test
    void testEmailNaoExiste() {
        EmailNaoExisteException exception = new EmailNaoExisteException("test@example.com");

        ResponseEntity<Erro> result = controllerAdvice.emailNaoExiste(exception);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("404", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Endereço de e-mail não existe: test@example.com", result.getBody().getMensagem());
    }

    @Test
    void testErroAoConstruirEmail() {
        MessagingException exception = new MessagingException("Failed to create email");

        ResponseEntity<Erro> result = controllerAdvice.erroAoConstruirEmail(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("500", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Erro no envio do e-mail", result.getBody().getMensagem());
    }

    @Test
    void testErroAoEnviarEmail() {
        MailSendException exception = new MailSendException("Failed to send email", new RuntimeException("Connection failed"), Map.of());

        ResponseEntity<Erro> result = controllerAdvice.erroAoEnviarEmail(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("500", Objects.requireNonNull(result.getBody()).getCodigo());
        assertEquals("Erro no envio do e-mail", result.getBody().getMensagem());
    }

    @Test
    void testErroDeCobranca(){
        LocalDateTime agora = LocalDateTime.now();
        ErroDeCobrancaException exception = new ErroDeCobrancaException(new Cobranca(CobrancaEntity.StatusCobranca.PENDENTE,
                agora,
                null,
                100L,
                1L));
        ResponseEntity<Cobranca> result = controllerAdvice.pagamentoRecusado(exception);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(agora, Objects.requireNonNull(result.getBody()).getHoraSolicitacao());
        assertEquals(CobrancaEntity.StatusCobranca.PENDENTE, result.getBody().getStatus());
        assertEquals(100L, result.getBody().getValor());
        assertEquals(1L, result.getBody().getCiclista());
        assertNull(result.getBody().getHoraFinalizacao());
    }

    @Test
    void testCobrancaNotFound() {
        CobrancaNotFound exception = new CobrancaNotFound("Não foi possível encontrar a cobrança com o ID: 99");

        ResponseEntity<Erro> result = controllerAdvice.cobrancaNotFound(exception);

        assertEquals(404, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("404", result.getBody().getCodigo());
        assertEquals("Não foi possível encontrar a cobrança com o ID: 99", result.getBody().getMensagem());
    }
}