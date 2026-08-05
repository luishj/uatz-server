package br.com.uatz.server.dto.budget;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Escolha do cliente. Hoje chega pelo painel (o operador registra o número que
 * o cliente respondeu); o webhook do WhatsApp vai usar o mesmo serviço.
 */
public record BudgetRequestSelectionRequest(
        @NotNull(message = "optionNumber is required")
        @Min(value = 1, message = "optionNumber must be greater than zero")
        Integer optionNumber
) {
}
