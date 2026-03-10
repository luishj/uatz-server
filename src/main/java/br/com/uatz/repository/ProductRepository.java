package br.com.uatz.repository;

import br.com.uatz.model.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findOptionalById(Long id);

    List<Product> listAllProducts();
}
