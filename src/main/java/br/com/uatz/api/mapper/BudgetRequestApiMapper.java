package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.budget.BudgetItemResponse;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import java.util.List;

public final class BudgetRequestApiMapper {

    private BudgetRequestApiMapper() {
    }

    public static BudgetRequestResponse toResponse(BudgetRequest budgetRequest, List<BudgetItem> items) {
        return new BudgetRequestResponse(
                budgetRequest.getId(),
                budgetRequest.getClient().getId(),
                budgetRequest.getCity(),
                budgetRequest.getStatus(),
                budgetRequest.getCreatedAt(),
                items.stream().map(BudgetRequestApiMapper::toItemResponse).toList()
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

