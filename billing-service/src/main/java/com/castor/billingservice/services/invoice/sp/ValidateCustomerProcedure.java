package com.castor.billingservice.services.invoice.sp;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Llamador del Stored Procedure VALIDATE_CUSTOMER_EXISTS en Oracle.
 */
@Component
@RequiredArgsConstructor
public class ValidateCustomerProcedure {

    private final JdbcTemplate oracleJdbcTemplate;

    /**
     * Ejecuta el SP VALIDATE_CUSTOMER_EXISTS en Oracle.
     */
    public void execute(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("El ID de cliente no puede ser null");
        }

        byte[] rawUuid = uuidToRaw(customerId);

        oracleJdbcTemplate.update(
                "BEGIN VALIDATE_CUSTOMER_EXISTS(?); END;",
                ps -> ps.setBytes(1, rawUuid)
        );
    }

    /**
     * Convierte UUID (Java) → RAW(16) para Oracle.
     */
    private byte[] uuidToRaw(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
