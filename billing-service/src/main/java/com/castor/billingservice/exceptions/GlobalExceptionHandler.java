package com.castor.billingservice.exceptions;

import com.castor.billingservice.dtos.common.ApiResponse;
import com.castor.billingservice.dtos.common.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para estandarizar respuestas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de validación @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationError>>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ValidationError.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiResponse<List<ValidationError>> response = ApiResponse.<List<ValidationError>>builder()
                .success(false)
                .message("Errores de validación")
                .data(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Excepciones de negocio controladas
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .status(ex.getStatusCode())   // ← usar el status del BusinessException
                .build();

        return ResponseEntity
                .status(ex.getStatusCode())   // ← usar el status aquí también
                .body(response);
    }

    /**
     * Excepciones genéricas (último recurso)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message("Error interno del servidor")
                .data(null)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        ex.printStackTrace(); // opcional, puedes dejarlo por debugging

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
