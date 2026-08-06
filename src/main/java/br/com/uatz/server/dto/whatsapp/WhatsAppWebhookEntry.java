package br.com.uatz.server.dto.whatsapp;

import java.util.List;

public record WhatsAppWebhookEntry(
        String id,
        List<WhatsAppWebhookChange> changes
) {
}
