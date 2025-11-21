package com.castor.billingservice.services.invoice.sp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidateCustomerProcedureTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ValidateCustomerProcedure procedure;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeThrowsExceptionWhenCustomerIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> procedure.execute(null));
    }

    @Test
    void executeCallsJdbcTemplateWithCorrectArguments() {
        UUID id = UUID.randomUUID();

        procedure.execute(id);

        verify(jdbcTemplate).update(
                eq("BEGIN VALIDATE_CUSTOMER_EXISTS(?); END;"),
                any(PreparedStatementSetter.class) //
        );
    }

    @Test
    @SuppressWarnings("java:S3011") // 👈 para Sonar
    void uuidToRawGenerates16Bytes() throws Exception {
        UUID uuid = UUID.randomUUID();
        var method = ValidateCustomerProcedure.class
                .getDeclaredMethod("uuidToRaw", UUID.class);
        method.setAccessible(true); // necesario para probar método privado
        byte[] result = (byte[]) method.invoke(procedure, uuid);
        assertEquals(16, result.length);
    }
}
