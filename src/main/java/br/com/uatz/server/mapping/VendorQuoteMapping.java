package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.vendorquote.VendorQuoteDetailsResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteItemResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.model.VendorQuoteItem;
import br.com.uatz.model.VendorQuote;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public final class VendorQuoteMapping {

    private VendorQuoteMapping() {
    }

    public static VendorQuoteResponse toResponse(VendorQuote vendorQuote) {
        return new VendorQuoteResponse(
                vendorQuote.getId(),
                vendorQuote.getRequest().getId(),
                vendorQuote.getVendor().getId(),
                vendorQuote.getVendor().getName(),
                vendorQuote.getTotalPrice(),
                vendorQuote.getMessage(),
                vendorQuote.getCreatedAt()
        );
    }

    public static VendorQuoteDetailsResponse toDetailsResponse(VendorQuote vendorQuote) {
        return new VendorQuoteDetailsResponse(
                vendorQuote.getId(),
                vendorQuote.getRequest().getId(),
                vendorQuote.getRequest().getCreatedAt(),
                vendorQuote.getVendor().getId(),
                vendorQuote.getVendor().getName(),
                vendorQuote.getTotalPrice(),
                vendorQuote.getMessage(),
                vendorQuote.getCreatedAt(),
                vendorQuote.getItems().stream()
                        .map(VendorQuoteMapping::toItemResponse)
                        .toList()
        );
    }

    public static VendorQuoteSummaryResponse toSummary(Long requestId, List<VendorQuote> quotes) {
        List<VendorQuoteResponse> responses = quotes.stream()
                .map(VendorQuoteMapping::toResponse)
                .toList();

        if (responses.isEmpty()) {
            return new VendorQuoteSummaryResponse(requestId, 0, null, null, null, null, responses);
        }

        BigDecimal lowestPrice = responses.stream()
                .map(VendorQuoteResponse::totalPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal highestPrice = responses.stream()
                .map(VendorQuoteResponse::totalPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal total = responses.stream()
                .map(VendorQuoteResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averagePrice = total.divide(BigDecimal.valueOf(responses.size()), 2, RoundingMode.HALF_UP);

        VendorQuoteResponse bestQuote = responses.stream()
                .min(Comparator.comparing(VendorQuoteResponse::totalPrice))
                .orElse(null);

        return new VendorQuoteSummaryResponse(
                requestId,
                responses.size(),
                lowestPrice,
                highestPrice,
                averagePrice,
                bestQuote,
                responses
        );
    }

    private static VendorQuoteItemResponse toItemResponse(VendorQuoteItem item) {
        return new VendorQuoteItemResponse(
                item.getId(),
                item.getBudgetItemId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnit(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
