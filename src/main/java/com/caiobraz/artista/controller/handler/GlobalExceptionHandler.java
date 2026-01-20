package com.caiobraz.artista.controller.handler;

import java.security.SignatureException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.caiobraz.artista.service.exception.AuthException;
import com.caiobraz.artista.service.exception.BusinessException;
import com.caiobraz.artista.service.exception.NotFoundException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;
        log.warn(exception.getMessage());

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty("mensagem", "Usuário ou senha incorreto");

            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("mensagem", "Conta bloqueada");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            errorDetail.setProperty("mensagem", "Sem autorização para acessar esse recurso");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            errorDetail.setProperty("mensagem", "Token JWT inválido");
        }

        if (errorDetail == null) {
            log.error(exception.getMessage(), exception);

            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
            errorDetail.setProperty("mensagem", "Erro interno");
        }

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        log.error(exception.getMessage(), exception);

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty("mensagem", "Token JWT expirado");

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {
        String mensagem = messageSource.getMessage(exception.getMessage(), exception.getArgs(), this.getLocale());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException exception) {
        String mensagem = messageSource.getMessage(exception.getMessage(), exception.getArgs(), this.getLocale());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, mensagem);
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthException.class)
    public ProblemDetail handleAccessDeniedException(AuthException exception) {
        String mensagem = messageSource.getMessage(exception.getMessage(), exception.getArgs(), this.getLocale());

        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, mensagem);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleNotFoundException(HandlerMethodValidationException exception) {
        String mensagem = messageSource.getMessage("erro.dadosInvalidos", null, this.getLocale());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensagem);
        for (ParameterValidationResult result : exception.getValueResults()) {
            for (MessageSourceResolvable resolvableError : result.getResolvableErrors()) {
                var field = ((FieldError) resolvableError).getField();
                var error = resolvableError.getDefaultMessage();
                errorDetail.setProperty(field, error);
            }
        }

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarExcecao(MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = exception.getBody();
        int i = 1;
        for (Object detailMessageArgument : exception.getDetailMessageArguments()) {
            if (detailMessageArgument.toString().isBlank()) continue;

            problemDetail.setProperty(i++ + "", detailMessageArgument);
        }
        return problemDetail;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail handleNotFoundException(org.springframework.dao.DataIntegrityViolationException exception) {
        log.error(exception.getMessage(), exception);
        String mensagem = messageSource.getMessage("erro.interno", null, Locale.getDefault());

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, mensagem);
        errorDetail.setProperty("mensagem", mensagem);

        return errorDetail;
    }

    private Locale getLocale() {
        return Locale.getDefault();
    }

}
