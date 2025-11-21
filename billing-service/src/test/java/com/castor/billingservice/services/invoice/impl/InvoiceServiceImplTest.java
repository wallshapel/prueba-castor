package com.castor.billingservice.services.invoice.impl;

import com.castor.billingservice.dtos.invoice.input.InvoiceInputDto;
import com.castor.billingservice.dtos.invoice.input.InvoiceItemDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceCalculationResponseDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceOutputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;
import com.castor.billingservice.entities.invoice.Invoice;
import com.castor.billingservice.exceptions.BusinessException;
import com.castor.billingservice.repositories.invoice.InvoiceRepository;
import com.castor.billingservice.services.customer.CustomerService;
import com.castor.billingservice.services.invoice.python.InvoiceCalculatorClient;
import com.castor.billingservice.services.invoice.sp.ValidateCustomerProcedure;
import com.castor.billingservice.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ValidateCustomerProcedure spValidator;

    @Mock
    private Mapper mapper;

    @Mock
    private InvoiceCalculatorClient calculatorClient;

    @InjectMocks
    private InvoiceServiceImpl service;

    private CustomerOutputDto activeCustomer;
    private CustomerOutputDto inactiveCustomer;
    private InvoiceInputDto validInput;
    private Invoice invoiceEntity;
    private InvoiceOutputDto invoiceDto;
    private InvoiceCalculationResponseDto calcResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // --- Cliente Activo ---
        activeCustomer = new CustomerOutputDto();
        activeCustomer.setId(UUID.randomUUID());
        activeCustomer.setName("John Doe");
        activeCustomer.setEmail("john@example.com");
        activeCustomer.setActive(true);

        // --- Cliente Inactivo ---
        inactiveCustomer = new CustomerOutputDto();
        inactiveCustomer.setId(UUID.randomUUID());
        inactiveCustomer.setName("Inactive");
        inactiveCustomer.setEmail("inactive@example.com");
        inactiveCustomer.setActive(false);

        // --- Ítem válido (Acorde a tu DTO real InvoiceItemDto) ---
        InvoiceItemDto item = new InvoiceItemDto();
        item.setProduct("Laptop Lenovo");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(1);

        // --- Input válido ---
        validInput = new InvoiceInputDto();
        validInput.setCustomerId(activeCustomer.getId());
        validInput.setItems(List.of(item));

        // --- Respuesta simulada del microservicio Python ---
        calcResponse = new InvoiceCalculationResponseDto();
        calcResponse.setSubtotal(new BigDecimal("100"));
        calcResponse.setTax(new BigDecimal("19"));
        calcResponse.setDiscount(new BigDecimal("0"));
        calcResponse.setTotal(new BigDecimal("119"));

        // --- Entidad Invoice simulada (previa al mapper) ---
        invoiceEntity = new Invoice();
        invoiceEntity.setId(UUID.randomUUID());
        invoiceEntity.setCustomerId(activeCustomer.getId());
        invoiceEntity.setSubtotal(calcResponse.getSubtotal());
        invoiceEntity.setTax(calcResponse.getTax());
        invoiceEntity.setDiscount(calcResponse.getDiscount());
        invoiceEntity.setTotal(calcResponse.getTotal());
        invoiceEntity.setCreatedAt(LocalDateTime.now());

        // --- DTO que devolverá el mapper ---
        invoiceDto = new InvoiceOutputDto();
        invoiceDto.setId(invoiceEntity.getId());
        invoiceDto.setCustomerId(invoiceEntity.getCustomerId());
        invoiceDto.setSubtotal(invoiceEntity.getSubtotal());
        invoiceDto.setTax(invoiceEntity.getTax());
        invoiceDto.setDiscount(invoiceEntity.getDiscount());
        invoiceDto.setTotal(invoiceEntity.getTotal());
        invoiceDto.setCreatedAt(invoiceEntity.getCreatedAt());
    }


    // ----------------------------------------------------------------------
    // TEST: findAll
    // ----------------------------------------------------------------------
    @Test
    void testFindAll() {
        when(invoiceRepository.findAll()).thenReturn(List.of(invoiceEntity));
        when(mapper.toDto(invoiceEntity, InvoiceOutputDto.class)).thenReturn(invoiceDto);

        List<InvoiceOutputDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(invoiceDto.getId(), result.get(0).getId());
    }

    // ----------------------------------------------------------------------
    // TEST: findByCustomerId
    // ----------------------------------------------------------------------
    @Test
    void testFindByCustomerId() {
        UUID customerId = UUID.randomUUID();

        when(customerService.findById(customerId)).thenReturn(activeCustomer);
        when(invoiceRepository.findByCustomerId(customerId)).thenReturn(List.of(invoiceEntity));
        when(mapper.toDto(invoiceEntity, InvoiceOutputDto.class)).thenReturn(invoiceDto);

        List<InvoiceOutputDto> result = service.findByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(invoiceDto.getId(), result.get(0).getId());
    }

    // ----------------------------------------------------------------------
    // TEST: create OK
    // ----------------------------------------------------------------------
    @Test
    void createSuccess() {

        when(customerService.findById(validInput.getCustomerId()))
                .thenReturn(activeCustomer);

        when(calculatorClient.calculate(validInput.getItems()))
                .thenReturn(calcResponse);

        when(invoiceRepository.save(any(Invoice.class)))
                .thenReturn(invoiceEntity);

        when(mapper.toDto(invoiceEntity, InvoiceOutputDto.class))
                .thenReturn(invoiceDto);

        InvoiceOutputDto result = service.create(validInput);

        assertNotNull(result);
        assertEquals(invoiceDto.getTotal(), result.getTotal());

        verify(spValidator).execute(validInput.getCustomerId());
    }

    // ----------------------------------------------------------------------
    // TEST: create cliente inactivo
    // ----------------------------------------------------------------------
    @Test
    void createInactiveCustomerThrowsException() {

        when(customerService.findById(any()))
                .thenReturn(inactiveCustomer);

        assertThrows(BusinessException.class, () -> service.create(validInput));
    }

    // ----------------------------------------------------------------------
    // TEST: create sin items
    // ----------------------------------------------------------------------
    @Test
    void createNoItemsThrowsException() {

        InvoiceInputDto emptyItemsInput = new InvoiceInputDto();
        emptyItemsInput.setCustomerId(UUID.randomUUID());
        emptyItemsInput.setItems(Collections.emptyList());

        when(customerService.findById(any()))
                .thenReturn(activeCustomer);

        assertThrows(BusinessException.class, () -> service.create(emptyItemsInput));
    }
}
