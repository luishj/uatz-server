package br.com.uatz.server.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Conteúdo da mudança notificada. Além de {@code messages}, a Cloud API usa o
 * mesmo evento para entregar {@code statuses} (entregue, lido), que não são
 * tratados aqui.
 */
public record WhatsAppWebhookValue(
        @JsonProperty("messaging_product")
        String messagingProduct,
        List<WhatsAppWebhookMessage> messages
) {
}
