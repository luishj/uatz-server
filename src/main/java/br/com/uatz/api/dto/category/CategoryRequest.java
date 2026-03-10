package br.com.uatz.api.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must have at most 120 characters")
        String name,
        Long parentId
) {
}

