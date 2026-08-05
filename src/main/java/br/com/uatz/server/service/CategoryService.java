package br.com.uatz.server.service;

import br.com.uatz.server.dto.category.CategoryRequest;
import br.com.uatz.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category create(CategoryRequest request);

    Optional<Category> findById(Long id);

    List<Category> findAll();
}

