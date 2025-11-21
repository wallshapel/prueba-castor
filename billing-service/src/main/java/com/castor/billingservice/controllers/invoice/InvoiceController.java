package com.castor.billingservice.controllers.invoice;

import com.castor.billingservice.dtos.common.ApiResponse;
import com.castor.billingservice.dtos.invoice.input.InvoiceInputDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceOutputDto;
import com.castor.billingservice.services.invoice.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar facturas.
 */
@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceOutputDto>>> getAll() {
        ApiResponse<List<InvoiceOutputDto>> response = ApiResponse.<List<InvoiceOutputDto>>builder()
                .success(true)
                .message("Listado de facturas obtenido correctamente")
                .data(service.findAll())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{customerId}")
    public ResponseEntity<ApiResponse<List<InvoiceOutputDto>>> getByCustomer(
            @PathVariable UUID customerId
    ) {
        ApiResponse<List<InvoiceOutputDto>> response = ApiResponse.<List<InvoiceOutputDto>>builder()
                .success(true)
                .message("Listado de facturas del cliente obtenido correctamente")
                .data(service.findByCustomerId(customerId))
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceOutputDto>> create(
            @Valid @RequestBody InvoiceInputDto dto
    ) {
        InvoiceOutputDto created = service.create(dto);

        ApiResponse<InvoiceOutputDto> response = ApiResponse.<InvoiceOutputDto>builder()
                .success(true)
                .message("Factura creada correctamente")
                .data(created)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
