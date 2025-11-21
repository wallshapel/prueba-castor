package com.castor.billingservice.dtos.common;

import lombok.Builder;
import lombok.Data;

/**
 * Representa un error de validación en un campo.
 */
@Data
@Builder
public class ValidationError {
    private String field;
    private String message;
}
