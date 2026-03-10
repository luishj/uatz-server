package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.category.CategoryResponse;
import br.com.uatz.model.entity.Category;

public final class CategoryApiMapper {

    private CategoryApiMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
        );
    }
}

