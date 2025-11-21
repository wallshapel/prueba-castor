package com.castor.billingservice.controllers.invoice;

import com.castor.billingservice.dtos.invoice.input.InvoiceInputDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceOutputDto;
import com.castor.billingservice.services.invoice.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    private static final String JSON_SUCCESS = "$.success";
    private static final String JSON_MESSAGE = "$.message";
    private static final String JSON_STATUS = "$.status";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService service;

    private InvoiceOutputDto exampleInvoice;

    @BeforeEach
    void setup() {
        exampleInvoice = InvoiceOutputDto.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("190.0"))
                .discount(new BigDecimal("0.0"))
                .total(new BigDecimal("1190.0"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(exampleInvoice));

        mockMvc.perform(get("/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Listado de facturas obtenido correctamente"))
                .andExpect(jsonPath("$.data[0].total").value(1190.0))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).findAll();
    }

    @Test
    void testGetByCustomer() throws Exception {
        UUID customerId = exampleInvoice.getCustomerId();

        Mockito.when(service.findByCustomerId(eq(customerId)))
                .thenReturn(List.of(exampleInvoice));

        mockMvc.perform(get("/facturas/cliente/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Listado de facturas del cliente obtenido correctamente"))
                .andExpect(jsonPath("$.data[0].customerId").value(customerId.toString()))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).findByCustomerId(customerId);
    }

    @Test
    void testCreate() throws Exception {
        Mockito.when(service.create(any(InvoiceInputDto.class))).thenReturn(exampleInvoice);

        mockMvc.perform(
                        post("/facturas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "customerId": "%s",
                                    "items": [
                                        {
                                            "productId": "11111111-1111-1111-1111-111111111111",
                                            "quantity": 2,
                                            "price": 500.0
                                        }
                                    ]
                                }
                                """.formatted(exampleInvoice.getCustomerId()))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Factura creada correctamente"))
                .andExpect(jsonPath("$.data.total").value(1190.0))
                .andExpect(jsonPath(JSON_STATUS).value(201));

        Mockito.verify(service).create(any(InvoiceInputDto.class));
    }
}
