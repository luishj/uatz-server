package br.com.uatz.server.dto.vendorquote;

import java.math.BigDecimal;

public record VendorQuoteItemResponse(
        Long id,
        Long budgetItemId,
        String productName,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
