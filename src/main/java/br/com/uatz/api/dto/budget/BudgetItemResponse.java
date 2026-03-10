package br.com.uatz.api.dto.budget;

import java.math.BigDecimal;

public record BudgetItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal quantity,
        String unit
) {
}

