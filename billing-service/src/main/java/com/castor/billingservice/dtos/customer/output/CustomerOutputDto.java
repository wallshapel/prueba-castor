package com.castor.billingservice.dtos.customer.output;

import lombok.Data;

import java.util.UUID;

/**
 * DTO de salida para retornar información de clientes.
 */
@Data
public class CustomerOutputDto {
    private UUID id;
    private String name;
    private String email;
    private Boolean active;
}
