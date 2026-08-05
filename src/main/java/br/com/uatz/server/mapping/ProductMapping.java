package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.product.ProductResponse;
import br.com.uatz.model.Product;

public final class ProductMapping {

    private ProductMapping() {
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}

