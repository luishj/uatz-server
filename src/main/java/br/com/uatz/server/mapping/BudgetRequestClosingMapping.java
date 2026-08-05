package br.com.uatz.server.mapping;

import br.com.uatz.model.VendorQuote;
import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionResponse;

public final class BudgetRequestClosingMapping {

    private BudgetRequestClosingMapping() {
    }

    public static BudgetRequestQuoteOptionResponse toOptionResponse(VendorQuote vendorQuote) {
        return new BudgetRequestQuoteOptionResponse(
                vendorQuote.getOptionNumber(),
                vendorQuote.getId(),
                vendorQuote.getVendor().getId(),
                vendorQuote.getVendor().getName(),
                vendorQuote.getTotalPrice()
        );
    }
}
