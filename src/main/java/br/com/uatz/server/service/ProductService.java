package br.com.uatz.server.service;

import br.com.uatz.server.dto.product.ProductRequest;
import br.com.uatz.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product create(ProductRequest request);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    /**
     * Semeia um apelido SEED para cada produto do catálogo. Backfill único para
     * os produtos criados antes do casamento por apelido existir; é idempotente
     * (apelido repetido é ignorado). Retorna quantos produtos foram varridos.
     */
    int semearApelidosDosProdutos();
}

