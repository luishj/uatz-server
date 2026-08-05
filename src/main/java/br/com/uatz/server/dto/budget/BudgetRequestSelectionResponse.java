package br.com.uatz.server.dto.budget;

import br.com.uatz.model.enumerador.BudgetRequestStatus;
import java.time.LocalDateTime;

/**
 * Resultado do fechamento do pedido.
 *
 * <p>Os campos de contato só vêm preenchidos para quem pode vê-los: admin e
 * operador sempre; o fornecedor apenas quando a cotação escolhida é a dele. Para
 * um fornecedor que não foi escolhido, {@code selectedOption} e os contatos vêm
 * nulos — o pedido está fechado, mas não com ele.</p>
 */
public record BudgetRequestSelectionResponse(
        Long requestId,
        BudgetRequestStatus status,
        LocalDateTime closedAt,
        BudgetRequestQuoteOptionResponse selectedOption,
        String clientPhone,
        String vendorPhone,
        String clientMessage,
        String clientLinkToVendor,
        String vendorLinkToClient
) {
}
