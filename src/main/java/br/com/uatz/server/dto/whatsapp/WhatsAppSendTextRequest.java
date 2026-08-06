package br.com.uatz.server.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WhatsAppSendTextRequest(
        @JsonProperty("preview_url")
        Boolean previewUrl,
        String body
) {
}
