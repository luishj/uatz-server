package br.com.uatz.server.dto.whatsapp;

/**
 * Mensagem recebida. {@code from} é o telefone do cliente em formato
 * internacional apenas com dígitos e {@code type} indica o conteúdo — só
 * {@code text} é processado.
 */
public record WhatsAppWebhookMessage(
        String id,
        String from,
        String timestamp,
        String type,
        WhatsAppWebhookText text
) {
}
