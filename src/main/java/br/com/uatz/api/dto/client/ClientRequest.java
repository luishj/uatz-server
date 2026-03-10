package br.com.uatz.api.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank(message = "phone is required")
        @Size(max = 30, message = "phone must have at most 30 characters")
        String phone,
        @Size(max = 120, message = "city must have at most 120 characters")
        String city,
        @Size(max = 60, message = "state must have at most 60 characters")
        String state
) {
}
