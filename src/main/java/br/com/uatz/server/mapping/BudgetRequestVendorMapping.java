package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.budget.BudgetRequestVendorResponse;
import br.com.uatz.model.BudgetRequestVendor;

public final class BudgetRequestVendorMapping {

    private BudgetRequestVendorMapping() {
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
