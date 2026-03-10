package br.com.uatz.api.dto.category;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId
) {
}

