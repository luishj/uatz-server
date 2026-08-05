package br.com.uatz.server.dto.budget;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Retorno do envio das opções de orçamento ao cliente. {@code clientMessage} é
 * exatamente o texto entregue ao gateway de WhatsApp e gravado na conversa.
 */
public record BudgetRequestQuoteOptionsResponse(
        Long requestId,
        LocalDateTime sentAt,
        String clientMessage,
        List<BudgetRequestQuoteOptionResponse> options
) {
}
