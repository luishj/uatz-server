package br.com.uatz.api.dto.vendorquote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record VendorQuoteRequest(
        @NotNull(message = "requestId is required")
        Long requestId,
        @NotNull(message = "vendorId is required")
        Long vendorId,
        @NotEmpty(message = "items are required")
        List<@Valid VendorQuoteItemRequest> items,
        @Size(max = 4000, message = "message must have at most 4000 characters")
        String message
) {
}
