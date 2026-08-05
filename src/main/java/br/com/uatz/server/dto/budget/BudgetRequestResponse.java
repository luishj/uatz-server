package br.com.uatz.server.dto.budget;

import br.com.uatz.model.enumerador.BudgetRequestStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido de orçamento. {@code clientPhone} só vem preenchido para admin e
 * operador e para o fornecedor cuja cotação foi escolhida — antes do fechamento
 * o contato do cliente fica fechado para os fornecedores.
 */
public record BudgetRequestResponse(
        Long id,
        Long clientId,
        String clientPhone,
        String city,
        BudgetRequestStatus status,
        String sourceChannel,
        String sourceMessage,
        LocalDateTime createdAt,
        LocalDateTime quotesSentAt,
        LocalDateTime closedAt,
        Long selectedQuoteId,
        Long selectedVendorId,
        List<BudgetItemResponse> items
) {
}
