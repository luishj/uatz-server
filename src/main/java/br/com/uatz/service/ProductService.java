package br.com.uatz.service;

import br.com.uatz.api.dto.product.ProductRequest;
import br.com.uatz.model.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product create(ProductRequest request);

    Optional<Product> findById(Long id);

    List<Product> findAll();
}

