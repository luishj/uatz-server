package br.com.uatz.server.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Corpo do POST {@code /{phoneNumberId}/messages} da WhatsApp Cloud API para
 * mensagem de texto livre.
 */
public record WhatsAppSendMessageRequest(
        @JsonProperty("messaging_product")
        String messagingProduct,
        @JsonProperty("recipient_type")
        String recipientType,
        String to,
        String type,
        WhatsAppSendTextRequest text
) {
}
