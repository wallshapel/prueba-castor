package com.castor.billingservice.dtos.customer.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para crear o actualizar clientes.
 */
@Data
public class CustomerInputDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo tiene un formato inválido")
    private String email;

    @NotNull(message = "El estado activo es requerido")
    private Boolean active;
}
