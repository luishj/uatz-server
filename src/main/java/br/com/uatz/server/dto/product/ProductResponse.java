package br.com.uatz.server.dto.product;

public record ProductResponse(
        Long id,
        String name,
        Long categoryId,
        String categoryName
) {
}

