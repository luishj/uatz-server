package br.com.uatz.server.dto.vendorquote;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VendorQuoteItemRequest(
        @NotNull(message = "budgetItemId is required")
        Long budgetItemId,
        @NotNull(message = "unitPrice is required")
        @DecimalMin(value = "0.00", message = "unitPrice must be zero or greater")
        BigDecimal unitPrice
) {
}
