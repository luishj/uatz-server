package br.com.uatz.service;

import br.com.uatz.api.dto.category.CategoryRequest;
import br.com.uatz.model.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category create(CategoryRequest request);

    Optional<Category> findById(Long id);

    List<Category> findAll();
}

