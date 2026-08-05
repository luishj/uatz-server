package br.com.uatz.server.dto.whatsapp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WhatsAppSimulationRequest(
        @NotBlank(message = "phone is required")
        @Size(max = 30, message = "phone must have at most 30 characters")
        String phone,
        @Size(max = 120, message = "city must have at most 120 characters")
        String city,
        @Size(max = 60, message = "state must have at most 60 characters")
        String state,
        @NotBlank(message = "message is required")
        @Size(max = 4000, message = "message must have at most 4000 characters")
        String message
) {
}
