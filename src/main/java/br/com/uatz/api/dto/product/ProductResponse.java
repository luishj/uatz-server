package br.com.uatz.api.dto.product;

public record ProductResponse(
        Long id,
        String name,
        Long categoryId,
        String categoryName
) {
}

