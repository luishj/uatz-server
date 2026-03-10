package br.com.uatz.repository.impl;

import java.util.List;
import java.util.Optional;

import br.com.uatz.model.entity.Product;
import br.com.uatz.repository.ProductRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository, PanacheRepositoryBase<Product, Long> {

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
