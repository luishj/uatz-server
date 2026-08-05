package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.budget.BudgetItemResponse;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.model.BudgetItem;
import br.com.uatz.model.BudgetRequest;
import java.util.List;

public final class BudgetRequestMapping {

    private BudgetRequestMapping() {
    }

    public static BudgetRequestResponse toResponse(BudgetRequest budgetRequest, List<BudgetItem> items) {
        return new BudgetRequestResponse(
                budgetRequest.getId(),
                budgetRequest.getClient().getId(),
                budgetRequest.getClient().getPhone(),
                budgetRequest.getCity(),
                budgetRequest.getStatus(),
                budgetRequest.getSourceChannel(),
                budgetRequest.getSourceMessage(),
                budgetRequest.getCreatedAt(),
                items.stream().map(BudgetRequestMapping::toItemResponse).toList()
        );
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
