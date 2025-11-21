package com.castor.billingservice.dtos.invoice.output;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO usado para recibir el cálculo del microservicio Python.
 */
@Data
public class InvoiceCalculationResponseDto {

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
}
