package com.caiobraz.artista.controller.handler;

import java.security.SignatureException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
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
import com.caiobraz.artista.service.exception.SystemException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        log.error(exception.getMessage(), exception);

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("erro.interno"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        log.error(exception.getMessage(), exception);

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("auth.tokenExpirado"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("auth.usuarioIncorreto"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("auth.tokenInvalido"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthException.class)
    public ProblemDetail handleAccessDeniedException(AuthException exception) {
        String mensagem = getMessage(exception);

        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, mensagem);
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleSignatureException(AccountStatusException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("auth.contaBloqueada"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty("mensagem", getMessage("auth.semAutorizacao"));

        return errorDetail;
    }

    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {
        String mensagem = getMessage(exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException exception) {
        String mensagem = getMessage(exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, mensagem);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleNotFoundException(HandlerMethodValidationException exception) {
        String mensagem = getMessage("erro.dadosInvalidos");
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

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String mensagem = getMessage(exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail handleNotFoundException(org.springframework.dao.DataIntegrityViolationException exception) {
        log.error(exception.getMessage(), exception);
        String mensagem = getMessage("erro.interno");

        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, mensagem);
        errorDetail.setProperty("mensagem", mensagem);

        return errorDetail;
    }

    private String getMessage(Exception exception) {
        if (exception instanceof SystemException systemException) {
            try {
                return messageSource.getMessage(systemException.getMessage(), systemException.getArgs(), this.getLocale());
            } catch (NoSuchMessageException e) {
                return exception.getMessage();
            }
        }

        return exception.getMessage();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, this.getLocale());
    }

    private Locale getLocale() {
        return Locale.getDefault();
    }
}
