package com.castor.billingservice.dtos.common;

import lombok.Builder;
import lombok.Data;

/**
 * Formato estándar para respuestas API.
 */
@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer status; // código HTTP
}
