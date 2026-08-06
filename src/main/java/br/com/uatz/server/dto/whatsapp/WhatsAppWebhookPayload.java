package br.com.uatz.server.dto.whatsapp;

import java.util.List;

/**
 * Corpo do POST enviado pela WhatsApp Cloud API. Só os campos consumidos pelo
 * serviço estão mapeados; o restante do payload é ignorado por Jackson.
 */
public record WhatsAppWebhookPayload(
        String object,
        List<WhatsAppWebhookEntry> entry
) {
}
