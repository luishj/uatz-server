package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.vendorquote.VendorQuoteResponse;
import br.com.uatz.api.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.model.entity.VendorQuote;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public final class VendorQuoteApiMapper {

    private VendorQuoteApiMapper() {
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

    public static VendorQuoteSummaryResponse toSummary(Long requestId, List<VendorQuote> quotes) {
        List<VendorQuoteResponse> responses = quotes.stream()
                .map(VendorQuoteApiMapper::toResponse)
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
}
