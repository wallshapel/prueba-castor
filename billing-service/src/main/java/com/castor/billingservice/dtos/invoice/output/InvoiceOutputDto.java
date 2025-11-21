package com.castor.billingservice.dtos.invoice.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para facturas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceOutputDto {

    private UUID id;
    private UUID customerId;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;

    private LocalDateTime createdAt;
}
