package br.com.uatz.service.impl;

import br.com.uatz.api.dto.product.ProductRequest;
import br.com.uatz.model.entity.Category;
import br.com.uatz.model.entity.Product;
import br.com.uatz.repository.CategoryRepository;
import br.com.uatz.repository.ProductRepository;
import br.com.uatz.service.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findOptionalById(request.categoryId())
                    .orElseThrow(() -> new WebApplicationException("Category not found", Response.Status.NOT_FOUND));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findOptionalById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.listAllProducts();
    }
}
