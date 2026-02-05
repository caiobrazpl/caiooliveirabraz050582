package com.caiobraz.artista.service.exception;

public class NotFoundException extends SystemException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object[] args) {
        super(message, args);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message, Object[] args, Throwable cause) {
        super(message, args, cause);
    }
}
