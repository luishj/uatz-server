package br.com.uatz.service.impl;

import br.com.uatz.api.dto.category.CategoryRequest;
import br.com.uatz.model.entity.Category;
import br.com.uatz.repository.CategoryRepository;
import br.com.uatz.service.CategoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());

        if (request.parentId() != null) {
            Category parent = categoryRepository.findOptionalById(request.parentId())
                    .orElseThrow(() -> new WebApplicationException("Parent category not found", Response.Status.NOT_FOUND));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findOptionalById(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.listAllCategories();
    }
}
