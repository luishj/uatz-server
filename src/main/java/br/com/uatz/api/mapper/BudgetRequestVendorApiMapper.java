package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.budget.BudgetRequestVendorResponse;
import br.com.uatz.model.entity.BudgetRequestVendor;

public final class BudgetRequestVendorApiMapper {

    private BudgetRequestVendorApiMapper() {
    }

    public static BudgetRequestVendorResponse toResponse(BudgetRequestVendor budgetRequestVendor) {
        return new BudgetRequestVendorResponse(
                budgetRequestVendor.getId(),
                budgetRequestVendor.getVendor().getId(),
                budgetRequestVendor.getVendor().getName(),
                budgetRequestVendor.getVendor().getEmail(),
                budgetRequestVendor.getStatus(),
                budgetRequestVendor.getSentAt(),
                budgetRequestVendor.getViewedAt(),
                budgetRequestVendor.getRespondedAt(),
                budgetRequestVendor.getDeclinedAt()
        );
    }
}
