package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.product.ProductResponse;
import br.com.uatz.model.entity.Product;

public final class ProductApiMapper {

    private ProductApiMapper() {
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

