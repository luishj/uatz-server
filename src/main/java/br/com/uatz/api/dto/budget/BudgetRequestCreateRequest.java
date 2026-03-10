package br.com.uatz.api.dto.budget;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BudgetRequestCreateRequest(
        @NotNull(message = "clientId is required")
        Long clientId,
        String city,
        @Valid
        @Size(min = 1, message = "items must contain at least one item")
        List<BudgetItemRequest> items
) {
}
