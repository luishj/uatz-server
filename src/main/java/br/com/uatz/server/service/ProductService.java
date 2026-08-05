package br.com.uatz.server.service;

import br.com.uatz.server.dto.product.ProductRequest;
import br.com.uatz.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product create(ProductRequest request);

    Optional<Product> findById(Long id);

    List<Product> findAll();
}

