package com.castor.billingservice.controllers.customer;

import com.castor.billingservice.dtos.customer.input.CustomerInputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;
import com.castor.billingservice.services.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest carga únicamente el controlador, no el contexto completo
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String BASE_URL = "/clientes";
    private static final String JSON_SUCCESS = "$.success";
    private static final String JSON_MESSAGE = "$.message";
    private static final String JSON_STATUS = "$.status";
    private static final String CLIENT_NAME = "John Doe";
    private static final String CLIENT_EMAIL = "john@example.com";

    @Autowired
    private MockMvc mockMvc;

    // Se mockea el servicio porque es dependencia del controlador
    @MockBean
    private CustomerService service;

    private CustomerOutputDto exampleCustomer;

    @BeforeEach
    void setUp() {
        // Cliente de ejemplo que devolverá el mock
        exampleCustomer = new CustomerOutputDto();
        exampleCustomer.setId(UUID.randomUUID());
        exampleCustomer.setName(CLIENT_NAME);
        exampleCustomer.setEmail(CLIENT_EMAIL);
        exampleCustomer.setActive(true);
    }

    @Test
    void testGetAll() throws Exception {
        // Given: el servicio devuelve una lista con 1 cliente
        Mockito.when(service.findAll()).thenReturn(List.of(exampleCustomer));

        // When + Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Listado de clientes obtenido correctamente"))
                .andExpect(jsonPath("$.data[0].name").value(CLIENT_NAME))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).findAll();
    }

    @Test
    void testGetById() throws Exception {
        UUID id = exampleCustomer.getId();

        Mockito.when(service.findById(id)).thenReturn(exampleCustomer);

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Cliente obtenido correctamente"))
                .andExpect(jsonPath("$.data.name").value(CLIENT_NAME))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).findById(id);
    }

    @Test
    void testCreate() throws Exception {
        CustomerInputDto input = new CustomerInputDto();
        input.setName(CLIENT_NAME);
        input.setEmail(CLIENT_EMAIL);

        Mockito.when(service.create(any(CustomerInputDto.class))).thenReturn(exampleCustomer);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        // 🎯 Aquí convertimos manualmente el JSON para evitar usar ObjectMapper
                        .content("""
                                {
                                    "name": "John Doe",
                                    "email": "john@example.com",
                                    "active": true
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Cliente creado correctamente"))
                .andExpect(jsonPath("$.data.name").value(CLIENT_NAME))
                .andExpect(jsonPath(JSON_STATUS).value(201));

        Mockito.verify(service).create(any(CustomerInputDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = exampleCustomer.getId();

        Mockito.when(service.update(eq(id), any(CustomerInputDto.class))).thenReturn(exampleCustomer);

        mockMvc.perform(put(BASE_URL +  "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "John Updated",
                                    "email": "john@example.com",
                                    "active": true
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Cliente actualizado correctamente"))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).update(eq(id), any(CustomerInputDto.class));
    }

    @Test
    void testDelete() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doNothing().when(service).delete(id);

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_SUCCESS).value(true))
                .andExpect(jsonPath(JSON_MESSAGE).value("Cliente eliminado correctamente"))
                .andExpect(jsonPath(JSON_STATUS).value(200));

        Mockito.verify(service).delete(id);
    }
}
