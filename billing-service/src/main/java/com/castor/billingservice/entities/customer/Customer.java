package com.castor.billingservice.entities.customer;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Entidad Customer (almacenada en PostgreSQL).
 */
@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private Boolean active;
}
