package com.castor.billingservice.services.invoice.impl;

import com.castor.billingservice.dtos.invoice.input.InvoiceInputDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceCalculationResponseDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceOutputDto;
import com.castor.billingservice.entities.invoice.Invoice;
import com.castor.billingservice.exceptions.BusinessException;
import com.castor.billingservice.repositories.invoice.InvoiceRepository;
import com.castor.billingservice.services.customer.CustomerService;
import com.castor.billingservice.services.invoice.InvoiceService;
import com.castor.billingservice.services.invoice.python.InvoiceCalculatorClient;
import com.castor.billingservice.services.invoice.sp.ValidateCustomerProcedure;
import com.castor.billingservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de facturas (Oracle).
 */
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;          // para validar existencia del cliente
    private final ValidateCustomerProcedure spValidator;    // para ejecutar el SP
    private final Mapper mapper;                            // para mapear entidades/DTOs
    private final InvoiceCalculatorClient calculatorClient;

    @Override
    public List<InvoiceOutputDto> findAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoice -> mapper.toDto(invoice, InvoiceOutputDto.class))
                .toList();
    }

    @Override
    public List<InvoiceOutputDto> findByCustomerId(UUID customerId) {

        // Reutilizamos validación del cliente en PostgreSQL
        customerService.findById(customerId);

        return invoiceRepository.findByCustomerId(customerId)
                .stream()
                .map(inv -> mapper.toDto(inv, InvoiceOutputDto.class))
                .toList();
    }

    @Override
    public InvoiceOutputDto create(InvoiceInputDto dto) {

        // 1. Validar que el cliente exista en PostgreSQL
        var customer = customerService.findById(dto.getCustomerId());

        // 1.1 Validar que el cliente esté activo
        if (Boolean.FALSE.equals(customer.getActive())) {
            throw new BusinessException("No se pueden crear facturas para un cliente inactivo", 400);
        }

        // 1.2 Validar que existan ítems
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("La factura debe contener al menos un ítem");
        }

        // 2. Ejecutar SP
        spValidator.execute(dto.getCustomerId());

        // 3. Llamar al microservicio Python para calcular totales
        InvoiceCalculationResponseDto calc =
                calculatorClient.calculate(dto.getItems());

        // 4. Crear entidad Invoice con los valores calculados
        Invoice invoice = new Invoice();
        invoice.setCustomerId(dto.getCustomerId());
        invoice.setSubtotal(calc.getSubtotal());
        invoice.setTax(calc.getTax());
        invoice.setDiscount(calc.getDiscount());
        invoice.setTotal(calc.getTotal());

        // 5. Guardar la factura en Oracle
        Invoice saved = invoiceRepository.save(invoice);

        // Convertir a DTO y retornar
        return mapper.toDto(saved, InvoiceOutputDto.class);
    }

}
