package br.com.uatz.api.dto.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BudgetItemRequest(
        Long productId,
        @NotBlank(message = "productName is required")
        @Size(max = 150, message = "productName must have at most 150 characters")
        String productName,
        @NotNull(message = "quantity is required")
        @DecimalMin(value = "0.01", message = "quantity must be greater than zero")
        BigDecimal quantity,
        @Size(max = 30, message = "unit must have at most 30 characters")
        String unit
) {
}
