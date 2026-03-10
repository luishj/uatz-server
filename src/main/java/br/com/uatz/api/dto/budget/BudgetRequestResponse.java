package br.com.uatz.api.dto.budget;

import br.com.uatz.model.enums.BudgetRequestStatus;
import java.time.LocalDateTime;
import java.util.List;

public record BudgetRequestResponse(
        Long id,
        Long clientId,
        String city,
        BudgetRequestStatus status,
        LocalDateTime createdAt,
        List<BudgetItemResponse> items
) {
}

