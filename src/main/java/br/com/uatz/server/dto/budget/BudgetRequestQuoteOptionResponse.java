package br.com.uatz.server.dto.budget;

import java.math.BigDecimal;

/**
 * Uma opção de orçamento como ela foi apresentada ao cliente no WhatsApp. O
 * {@code optionNumber} é o número que o cliente responde para escolher.
 */
public record BudgetRequestQuoteOptionResponse(
        Integer optionNumber,
        Long quoteId,
        Long vendorId,
        String vendorName,
        BigDecimal totalPrice
) {
}
