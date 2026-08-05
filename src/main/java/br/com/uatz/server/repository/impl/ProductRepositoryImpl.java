package br.com.uatz.server.repository.impl;

import java.util.List;
import java.util.Optional;

import br.com.uatz.model.Product;
import br.com.uatz.server.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductRepositoryImpl extends GenericRepositoryImpl<Product, Long> implements ProductRepository {

    @Override
    @Transactional
    public Product save(Product product) {
        persist(product);
        return product;
    }

    @Override
    public Optional<Product> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public List<Product> listAllProducts() {
        return listAll();
    }
}
