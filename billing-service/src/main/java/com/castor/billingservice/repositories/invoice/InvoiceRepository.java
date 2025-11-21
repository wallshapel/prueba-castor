package com.castor.billingservice.repositories.invoice;

import com.castor.billingservice.entities.invoice.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByCustomerId(UUID customerId);
}
