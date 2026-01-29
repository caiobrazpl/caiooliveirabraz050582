package com.caiobraz.artista.service.exception;

public class AuthException extends SystemException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Object[] args) {
        super(message, args);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(String message, Object[] args, Throwable cause) {
        super(message, args, cause);
    }
}
