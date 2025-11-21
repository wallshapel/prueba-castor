package com.castor.billingservice.services.invoice;

import com.castor.billingservice.dtos.invoice.input.InvoiceInputDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceOutputDto;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar facturas (Oracle).
 */
public interface InvoiceService {

    List<InvoiceOutputDto> findAll();

    List<InvoiceOutputDto> findByCustomerId(UUID customerId);

    InvoiceOutputDto create(InvoiceInputDto dto);
}
