package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.category.CategoryResponse;
import br.com.uatz.model.Category;

public final class CategoryMapping {

    private CategoryMapping() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
        );
    }
}

