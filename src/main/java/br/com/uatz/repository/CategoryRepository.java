package br.com.uatz.repository;

import br.com.uatz.model.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findOptionalById(Long id);

    List<Category> listAllCategories();
}
