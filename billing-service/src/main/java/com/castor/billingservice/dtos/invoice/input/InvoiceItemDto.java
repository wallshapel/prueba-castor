package com.castor.billingservice.dtos.invoice.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Ítem de factura enviado al microservicio Python.
 */
@Data
public class InvoiceItemDto {

    @NotNull(message = "El nombre del producto es obligatorio")
    private String product;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    private BigDecimal price;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que 0")
    private Integer quantity;
}
