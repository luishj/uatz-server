package br.com.uatz.server.repository;

import br.com.uatz.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends GenericRepository<Product, Long> {

    Product save(Product product);

    Optional<Product> findOptionalById(Long id);

    List<Product> listAllProducts();
}
