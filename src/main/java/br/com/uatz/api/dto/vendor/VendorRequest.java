package br.com.uatz.api.dto.vendor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VendorRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must have at most 150 characters")
        String name,
        @NotBlank(message = "phone is required")
        @Size(max = 30, message = "phone must have at most 30 characters")
        String phone,
        @Email(message = "email must be valid")
        @Size(max = 150, message = "email must have at most 150 characters")
        String email,
        @Size(max = 120, message = "city must have at most 120 characters")
        String city,
        @Size(max = 60, message = "state must have at most 60 characters")
        String state,
        Boolean active
) {
}
