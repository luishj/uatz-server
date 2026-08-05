package br.com.uatz.server.dto.category;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId
) {
}

