package br.com.uatz.api.dto.vendorquote;

import java.math.BigDecimal;
import java.util.List;

public record VendorQuoteSummaryResponse(
        Long requestId,
        Integer totalQuotes,
        BigDecimal lowestPrice,
        BigDecimal highestPrice,
        BigDecimal averagePrice,
        VendorQuoteResponse bestQuote,
        List<VendorQuoteResponse> quotes
) {
}
