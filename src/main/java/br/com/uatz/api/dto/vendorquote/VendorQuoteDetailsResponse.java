package br.com.uatz.api.dto.vendorquote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendorQuoteDetailsResponse(
        Long id,
        Long requestId,
        LocalDateTime requestCreatedAt,
        Long vendorId,
        String vendorName,
        BigDecimal totalPrice,
        String message,
        LocalDateTime createdAt,
        List<VendorQuoteItemResponse> items
) {
}
