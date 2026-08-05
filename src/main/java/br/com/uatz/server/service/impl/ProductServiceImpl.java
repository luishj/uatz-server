package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.product.ProductRequest;
import br.com.uatz.model.Category;
import br.com.uatz.model.Product;
import br.com.uatz.server.repository.CategoryRepository;
import br.com.uatz.server.repository.ProductRepository;
import br.com.uatz.server.service.ProductService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
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
                    .orElseThrow(() -> MessageBuilder.build(CloudMessage.CATEGORIA_NAO_ENCONTRADA, Status.NOT_FOUND));
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
