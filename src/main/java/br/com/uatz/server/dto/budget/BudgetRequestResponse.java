package br.com.uatz.server.dto.budget;

import br.com.uatz.model.enumerador.BudgetRequestStatus;
import java.time.LocalDateTime;
import java.util.List;

public record BudgetRequestResponse(
        Long id,
        Long clientId,
        String clientPhone,
        String city,
        BudgetRequestStatus status,
        String sourceChannel,
        String sourceMessage,
        LocalDateTime createdAt,
        List<BudgetItemResponse> items
) {
}
