package br.com.uatz.api.dto.vendorquote;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VendorQuoteRequest(
        @NotNull(message = "requestId is required")
        Long requestId,
        @NotNull(message = "vendorId is required")
        Long vendorId,
        @NotNull(message = "totalPrice is required")
        @DecimalMin(value = "0.01", message = "totalPrice must be greater than zero")
        BigDecimal totalPrice,
        @Size(max = 4000, message = "message must have at most 4000 characters")
        String message
) {
}
