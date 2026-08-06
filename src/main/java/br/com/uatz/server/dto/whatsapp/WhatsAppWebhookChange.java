package br.com.uatz.server.dto.whatsapp;

public record WhatsAppWebhookChange(
        String field,
        WhatsAppWebhookValue value
) {
}
