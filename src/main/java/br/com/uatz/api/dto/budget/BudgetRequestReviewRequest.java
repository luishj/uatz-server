package br.com.uatz.api.dto.budget;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BudgetRequestReviewRequest(
        @Size(max = 120, message = "city must have at most 120 characters")
        String city,
        @Valid
        @Size(min = 1, message = "items must contain at least one item")
        List<BudgetItemRequest> items
) {
}
