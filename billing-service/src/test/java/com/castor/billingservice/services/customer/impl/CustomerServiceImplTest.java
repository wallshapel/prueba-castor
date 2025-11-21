package com.castor.billingservice.services.customer.impl;

import com.castor.billingservice.dtos.customer.input.CustomerInputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;
import com.castor.billingservice.entities.customer.Customer;
import com.castor.billingservice.exceptions.BusinessException;
import com.castor.billingservice.repositories.customer.CustomerRepository;
import com.castor.billingservice.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceImplTest {

    private static final String CLIENT_NAME = "John Doe";
    private static final String CLIENT_EMAIL = "john@example.com";

    @Mock
    private CustomerRepository repository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer customerEntity;
    private CustomerOutputDto customerDto;
    private CustomerInputDto input;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        customerEntity = new Customer();
        customerEntity.setId(UUID.randomUUID());
        customerEntity.setName(CLIENT_NAME);
        customerEntity.setEmail(CLIENT_EMAIL);
        customerEntity.setActive(true);

        customerDto = new CustomerOutputDto();
        customerDto.setId(customerEntity.getId());
        customerDto.setName(CLIENT_NAME);
        customerDto.setEmail(CLIENT_EMAIL);
        customerDto.setActive(true);

        input = new CustomerInputDto();
        input.setName(CLIENT_NAME);
        input.setEmail(CLIENT_EMAIL);
        input.setActive(true);
    }

    // ---------------------------------------------------------
    // TEST: findAll()
    // ---------------------------------------------------------
    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(customerEntity));
        when(mapper.toDto(any(Customer.class), eq(CustomerOutputDto.class)))
                .thenReturn(customerDto);

        List<CustomerOutputDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(CLIENT_NAME, result.get(0).getName());
        verify(repository).findAll();
    }

    // ---------------------------------------------------------
    // TEST: findById() OK
    // ---------------------------------------------------------
    @Test
    void testFindById() {
        when(repository.findById(any())).thenReturn(Optional.of(customerEntity));
        when(mapper.toDto(any(Customer.class), eq(CustomerOutputDto.class)))
                .thenReturn(customerDto);

        CustomerOutputDto result = service.findById(customerEntity.getId());

        assertEquals(CLIENT_NAME, result.getName());
        verify(repository).findById(customerEntity.getId());
    }

    // ---------------------------------------------------------
    // TEST: findById() NOT FOUND
    // ---------------------------------------------------------
    @Test
    void findByIdNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.findById(UUID.randomUUID())
        );

        assertEquals("Cliente no encontrado", ex.getMessage());
    }


    // ---------------------------------------------------------
    // TEST: create() OK
    // ---------------------------------------------------------
    @Test
    void testCreate() {
        when(repository.existsByEmail(CLIENT_EMAIL)).thenReturn(false);
        when(mapper.toEntity(any(), eq(Customer.class))).thenReturn(customerEntity);
        when(repository.save(customerEntity)).thenReturn(customerEntity);
        when(mapper.toDto(customerEntity, CustomerOutputDto.class)).thenReturn(customerDto);

        CustomerOutputDto result = service.create(input);

        assertEquals(CLIENT_NAME, result.getName());
        verify(repository).save(customerEntity);
    }

    // ---------------------------------------------------------
    // TEST: create() → EMAIL DUPLICADO
    // ---------------------------------------------------------
    @Test
    void createEmailDuplicate() {
        when(repository.existsByEmail(CLIENT_EMAIL)).thenReturn(true);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.create(input)
        );

        assertEquals("Ya existe un cliente con ese correo electrónico", ex.getMessage());
    }

    // ---------------------------------------------------------
    // TEST: update() OK
    // ---------------------------------------------------------
    @Test
    void testUpdate() {
        when(repository.findById(any())).thenReturn(Optional.of(customerEntity));
        when(repository.existsByEmail(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(customerEntity);
        when(mapper.toDto(any(), eq(CustomerOutputDto.class))).thenReturn(customerDto);

        CustomerOutputDto result = service.update(customerEntity.getId(), input);

        assertEquals(CLIENT_NAME, result.getName());
        verify(repository).save(any());
    }

    // ---------------------------------------------------------
    // TEST: update() → NOT FOUND
    // ---------------------------------------------------------
    @Test
    void updateNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.update(UUID.randomUUID(), input)
        );

        assertEquals("Cliente no encontrado", ex.getMessage());
    }

    // ---------------------------------------------------------
    // TEST: update() → EMAIL DUPLICADO
    // ---------------------------------------------------------
    @Test
    void updateEmailDuplicate() {
        Customer other = new Customer();
        other.setId(UUID.randomUUID());
        other.setEmail("another@example.com");

        CustomerInputDto updatedDto = new CustomerInputDto();
        updatedDto.setName("John");
        updatedDto.setEmail("duplicate@example.com");
        updatedDto.setActive(true);

        when(repository.findById(any())).thenReturn(Optional.of(customerEntity));
        when(repository.existsByEmail("duplicate@example.com")).thenReturn(true);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.update(customerEntity.getId(), updatedDto)
        );

        assertEquals("Ya existe un cliente con ese correo electrónico", ex.getMessage());
    }

    // ---------------------------------------------------------
    // TEST: delete()
    // ---------------------------------------------------------
    @Test
    void testDelete() {
        when(repository.findById(any())).thenReturn(Optional.of(customerEntity));
        doNothing().when(repository).delete(customerEntity);

        assertDoesNotThrow(() -> service.delete(customerEntity.getId()));
        verify(repository).delete(customerEntity);
    }
}
