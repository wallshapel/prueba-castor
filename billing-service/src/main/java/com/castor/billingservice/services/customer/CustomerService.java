package com.castor.billingservice.services.customer;

import com.castor.billingservice.dtos.customer.input.CustomerInputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<CustomerOutputDto> findAll();

    CustomerOutputDto findById(UUID id);

    CustomerOutputDto create(CustomerInputDto dto);

    CustomerOutputDto update(UUID id, CustomerInputDto dto);

    void delete(UUID id);
}
