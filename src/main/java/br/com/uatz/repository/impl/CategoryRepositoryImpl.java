package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.Category;
import br.com.uatz.repository.CategoryRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepositoryImpl implements CategoryRepository, PanacheRepositoryBase<Category, Long> {

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
