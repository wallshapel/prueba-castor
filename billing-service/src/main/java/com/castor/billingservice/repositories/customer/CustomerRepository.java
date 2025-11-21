package com.castor.billingservice.repositories.customer;

import com.castor.billingservice.entities.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio para clientes (PostgreSQL).
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);
}
