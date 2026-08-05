package br.com.uatz.server.repository.impl;

import br.com.uatz.model.Category;
import br.com.uatz.server.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepositoryImpl extends GenericRepositoryImpl<Category, Long> implements CategoryRepository {

    @Override
    @Transactional
    public Category save(Category category) {
        persist(category);
        return category;
    }

    @Override
    public Optional<Category> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public List<Category> listAllCategories() {
        return listAll();
    }
}
