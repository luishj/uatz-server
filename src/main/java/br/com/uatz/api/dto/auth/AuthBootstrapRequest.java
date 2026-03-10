package br.com.uatz.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthBootstrapRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must have at most 150 characters")
        String name,
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 150, message = "email must have at most 150 characters")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 100, message = "password must have between 6 and 100 characters")
        String password
) {
}

