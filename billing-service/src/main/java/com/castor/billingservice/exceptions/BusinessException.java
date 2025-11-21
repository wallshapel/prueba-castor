package com.castor.billingservice.exceptions;

import lombok.Getter;

/**
 * Excepción para errores de negocio controlados.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int statusCode;

    public BusinessException(String message) {
        super(message);
        this.statusCode = 400;
    }

    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
