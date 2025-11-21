package com.castor.billingservice.dtos.invoice.input;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada para crear facturas.
 */
@Data
public class InvoiceInputDto {

    @NotNull(message = "El cliente es obligatorio")
    private UUID customerId;

    @NotNull(message = "Los ítems no pueden estar vacíos")
    private List<InvoiceItemDto> items;
}
