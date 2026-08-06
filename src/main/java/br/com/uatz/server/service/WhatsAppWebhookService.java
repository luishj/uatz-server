package br.com.uatz.server.service;

/**
 * Entrada das mensagens vindas da WhatsApp Cloud API.
 */
public interface WhatsAppWebhookService {

    /**
     * Responde à verificação que a Meta faz ao cadastrar a URL do webhook.
     * Devolve o desafio recebido quando o token confere.
     */
    String verify(String mode, String token, String challenge);

    /**
     * Processa a notificação recebida. O corpo entra como texto porque a
     * assinatura é calculada sobre os bytes originais.
     */
    void process(String signature, String payload);
}
