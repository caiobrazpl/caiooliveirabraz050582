package com.caiobraz.artista.service.exception;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

    private Object[] args;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Object[] args) {
        super(message);
        this.args = args;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(String message, Object[] args, Throwable cause) {
        super(message, cause);
        this.args = args;
    }
}
