package com.castor.billingservice.entities.invoice;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Invoice (almacenada en Oracle).
 */
@Data
@Entity
@Table(name = "INVOICES")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "CUSTOMER_ID", nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal tax;

    @Column(nullable = false)
    private BigDecimal discount;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
