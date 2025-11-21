package com.castor.billingservice.controllers.customer;

import com.castor.billingservice.dtos.common.ApiResponse;
import com.castor.billingservice.dtos.customer.input.CustomerInputDto;
import com.castor.billingservice.dtos.customer.output.CustomerOutputDto;
import com.castor.billingservice.services.customer.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar clientes.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping
    public ApiResponse<List<CustomerOutputDto>> getAll() {
        return ApiResponse.<List<CustomerOutputDto>>builder()
                .success(true)
                .message("Listado de clientes obtenido correctamente")
                .data(service.findAll())
                .status(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerOutputDto> getById(@PathVariable UUID id) {
        return ApiResponse.<CustomerOutputDto>builder()
                .success(true)
                .message("Cliente obtenido correctamente")
                .data(service.findById(id))
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerOutputDto>> create(@Valid @RequestBody CustomerInputDto dto) {
        CustomerOutputDto created = service.create(dto);

        ApiResponse<CustomerOutputDto> response = ApiResponse.<CustomerOutputDto>builder()
                .success(true)
                .message("Cliente creado correctamente")
                .data(created)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerOutputDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody CustomerInputDto dto) {
        CustomerOutputDto updated = service.update(id, dto);

        return ApiResponse.<CustomerOutputDto>builder()
                .success(true)
                .message("Cliente actualizado correctamente")
                .data(updated)
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        service.delete(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Cliente eliminado correctamente")
                .data(null)
                .status(HttpStatus.OK.value())
                .build();
    }

}
