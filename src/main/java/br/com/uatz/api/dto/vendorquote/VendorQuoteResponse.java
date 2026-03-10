package br.com.uatz.api.dto.vendorquote;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendorQuoteResponse(
        Long id,
        Long requestId,
        Long vendorId,
        String vendorName,
        BigDecimal totalPrice,
        String message,
        LocalDateTime createdAt
) {
}
