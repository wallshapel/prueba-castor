package com.castor.billingservice.services.invoice.python;

import com.castor.billingservice.dtos.common.ApiResponse;
import com.castor.billingservice.dtos.invoice.input.InvoiceItemDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceCalculationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceCalculatorClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InvoiceCalculatorClient client;

    private InvoiceItemDto itemDto;
    private ApiResponse<InvoiceCalculationResponseDto> apiResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Asignamos la URL requerida por la propiedad externalizada
        ReflectionTestUtils.setField(
                client,
                "calculatorUrl",
                "http://localhost:5001/calculate"
        );

        itemDto = new InvoiceItemDto();
        itemDto.setProduct("Producto A");
        itemDto.setPrice(new BigDecimal("100"));
        itemDto.setQuantity(1);

        InvoiceCalculationResponseDto calcDto = new InvoiceCalculationResponseDto();
        calcDto.setSubtotal(new BigDecimal("100"));
        calcDto.setTax(new BigDecimal("19"));
        calcDto.setDiscount(BigDecimal.ZERO);
        calcDto.setTotal(new BigDecimal("119"));

        apiResponse = ApiResponse.<InvoiceCalculationResponseDto>builder()
                .success(true)
                .message("OK")
                .data(calcDto)
                .status(200)
                .build();
    }

    @Test
    void calculateReturnsMappedResponse() {

        ResponseEntity<ApiResponse<InvoiceCalculationResponseDto>> fakeResponse =
                new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:5001/calculate"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(fakeResponse);

        InvoiceCalculationResponseDto result = client.calculate(List.of(itemDto));

        assertNotNull(result);
        assertEquals(new BigDecimal("119"), result.getTotal());

        verify(restTemplate).exchange(
                eq("http://localhost:5001/calculate"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void calculateNullBodyReturnsNull() {

        ResponseEntity<ApiResponse<InvoiceCalculationResponseDto>> fakeResponse =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(fakeResponse);

        InvoiceCalculationResponseDto result = client.calculate(List.of(itemDto));

        assertNull(result);
    }
}
