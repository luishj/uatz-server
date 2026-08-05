package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.budget.BudgetItemResponse;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.model.BudgetItem;
import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.VendorQuote;
import java.util.List;

public final class BudgetRequestMapping {

    private BudgetRequestMapping() {
    }

    /**
     * Resposta completa, com os dados do cliente. Uso de admin e operador.
     */
    public static BudgetRequestResponse toResponse(BudgetRequest budgetRequest, List<BudgetItem> items) {
        return toResponse(budgetRequest, items, true);
    }

    /**
     * Resposta para um fornecedor: o telefone do cliente só aparece quando a
     * cotação escolhida é a dele.
     */
    public static BudgetRequestResponse toVendorResponse(BudgetRequest budgetRequest, List<BudgetItem> items, Long vendorId) {
        return toResponse(budgetRequest, items, isSelectedVendor(budgetRequest, vendorId));
    }

    private static BudgetRequestResponse toResponse(BudgetRequest budgetRequest, List<BudgetItem> items, boolean exibirContatoCliente) {
        VendorQuote selectedQuote = budgetRequest.getSelectedQuote();

        return new BudgetRequestResponse(
                budgetRequest.getId(),
                budgetRequest.getClient().getId(),
                exibirContatoCliente ? budgetRequest.getClient().getPhone() : null,
                budgetRequest.getCity(),
                budgetRequest.getStatus(),
                budgetRequest.getSourceChannel(),
                budgetRequest.getSourceMessage(),
                budgetRequest.getCreatedAt(),
                budgetRequest.getQuotesSentAt(),
                budgetRequest.getClosedAt(),
                selectedQuote != null ? selectedQuote.getId() : null,
                selectedQuote != null ? selectedQuote.getVendor().getId() : null,
                items.stream().map(BudgetRequestMapping::toItemResponse).toList()
        );
    }

    private static boolean isSelectedVendor(BudgetRequest budgetRequest, Long vendorId) {
        VendorQuote selectedQuote = budgetRequest.getSelectedQuote();

        return vendorId != null
                && selectedQuote != null
                && vendorId.equals(selectedQuote.getVendor().getId());
    }

    private static BudgetItemResponse toItemResponse(BudgetItem item) {
        return new BudgetItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProductName(),
                item.getQuantity(),
                item.getUnit()
        );
    }
}
