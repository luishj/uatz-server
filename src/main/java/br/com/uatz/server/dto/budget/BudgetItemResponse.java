package br.com.uatz.server.dto.budget;

import java.math.BigDecimal;

public record BudgetItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal quantity,
        String unit
) {
}

