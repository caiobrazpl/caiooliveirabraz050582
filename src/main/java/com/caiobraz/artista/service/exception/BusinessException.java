package com.caiobraz.artista.service.exception;

public class BusinessException extends SystemException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object[] args) {
        super(message, args);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, Object[] args, Throwable cause) {
        super(message, args, cause);
    }
}
