package br.com.uatz.server.dto.whatsapp;

import java.util.List;

public record WhatsAppSendMessageResponse(
        List<WhatsAppSentMessageResponse> messages
) {
}
