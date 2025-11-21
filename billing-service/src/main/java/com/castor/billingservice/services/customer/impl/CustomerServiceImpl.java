package com.castor.billingservice.services.customer.impl;

import com.castor.billingservice.dtos.customer.input.CustomerInputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;
import com.castor.billingservice.entities.customer.Customer;
import com.castor.billingservice.exceptions.BusinessException;
import com.castor.billingservice.repositories.customer.CustomerRepository;
import com.castor.billingservice.services.customer.CustomerService;
import com.castor.billingservice.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de clientes.
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final Mapper mapper;

    @Override
    public List<CustomerOutputDto> findAll() {
        return repository.findAll()
                .stream()
                .map(c -> mapper.toDto(c, CustomerOutputDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerOutputDto findById(UUID id) {
        Customer customer = getCustomerOrThrow(id);
        return mapper.toDto(customer, CustomerOutputDto.class);
    }

    @Override
    public CustomerOutputDto create(CustomerInputDto dto) {
        validateEmailUniqueness(dto.getEmail());

        Customer entity = mapper.toEntity(dto, Customer.class);
        Customer saved = repository.save(entity);

        return mapper.toDto(saved, CustomerOutputDto.class);
    }

    @Override
    public CustomerOutputDto update(UUID id, CustomerInputDto dto) {
        Customer existing = getCustomerOrThrow(id);
        validateEmailUniquenessForUpdate(existing, dto);

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setActive(dto.getActive());

        Customer saved = repository.save(existing);
        return mapper.toDto(saved, CustomerOutputDto.class);
    }

    @Override
    public void delete(UUID id) {
        Customer customer = getCustomerOrThrow(id);
        repository.delete(customer);
    }

    // ===================================================================================
    // MÉTODOS PRIVADOS REUTILIZABLES
    // ===================================================================================

    /**
     * Obtiene el cliente por ID o lanza una BusinessException estándar.
     */
    private Customer getCustomerOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado", 404));
    }

    /**
     * Valida que el email no exista en la BD al crear un nuevo cliente.
     */
    private void validateEmailUniqueness(String email) {
        if (repository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un cliente con ese correo electrónico");
        }
    }

    /**
     * Valida duplicado de email cuando se está actualizando un cliente existente.
     */
    private void validateEmailUniquenessForUpdate(Customer existing, CustomerInputDto dto) {

        boolean emailChanged = existing.getEmail() != null &&
                !existing.getEmail().equalsIgnoreCase(dto.getEmail());

        if (emailChanged && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un cliente con ese correo electrónico");
        }
    }
}
