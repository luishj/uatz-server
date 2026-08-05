package br.com.uatz.server.repository;

import br.com.uatz.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends GenericRepository<Category, Long> {

    Category save(Category category);

    Optional<Category> findOptionalById(Long id);

    List<Category> listAllCategories();
}
