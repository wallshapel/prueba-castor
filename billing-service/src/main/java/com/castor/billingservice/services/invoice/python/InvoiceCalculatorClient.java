package com.castor.billingservice.services.invoice.python;

import com.castor.billingservice.dtos.common.ApiResponse;
import com.castor.billingservice.dtos.invoice.input.InvoiceItemDto;
import com.castor.billingservice.dtos.invoice.output.InvoiceCalculationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvoiceCalculatorClient {

    private final RestTemplate restTemplate;

    @Value("${external-services.invoice-calculator.url}")
    private String calculatorUrl;

    public InvoiceCalculationResponseDto calculate(List<InvoiceItemDto> items) {

        Map<String, Object> request = new HashMap<>();
        request.put("items", items);

        ResponseEntity<ApiResponse<InvoiceCalculationResponseDto>> response =
                restTemplate.exchange(
                        calculatorUrl,
                        HttpMethod.POST,
                        new org.springframework.http.HttpEntity<>(request),
                        new ParameterizedTypeReference<ApiResponse<InvoiceCalculationResponseDto>>() {}
                );

        ApiResponse<InvoiceCalculationResponseDto> apiResponse = response.getBody();

        return apiResponse != null ? apiResponse.getData() : null;
    }
}
