package br.com.uatz.server.dto.budget;

import br.com.uatz.model.enumerador.BudgetRequestVendorStatus;
import java.time.LocalDateTime;

public record BudgetRequestVendorResponse(
        Long id,
        Long vendorId,
        String vendorName,
        String vendorEmail,
        BudgetRequestVendorStatus status,
        LocalDateTime sentAt,
        LocalDateTime viewedAt,
        LocalDateTime respondedAt,
        LocalDateTime declinedAt
) {
}
