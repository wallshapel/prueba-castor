package com.castor.billingservice.utils;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Utilidad para mapear entidades y DTOs usando ModelMapper.
 */
@Component
@AllArgsConstructor
public class Mapper {

    private final ModelMapper modelMapper;

    public <D, T> D toDto(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    public <D, T> T toEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }
}
