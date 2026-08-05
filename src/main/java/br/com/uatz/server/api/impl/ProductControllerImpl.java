package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.ProductController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.product.ProductRequest;
import br.com.uatz.server.dto.product.ProductResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.ProductMapping;
import br.com.uatz.server.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class ProductControllerImpl implements ProductController {

    @Inject
    ProductService productService;

    @Override
    public Response create(ProductRequest request) {
        ProductResponse response = ProductMapping.toResponse(productService.create(request));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    public List<ProductResponse> findAll() {
        return productService.findAll()
                .stream()
                .map(ProductMapping::toResponse)
                .toList();
    }

    @Override
    public ProductResponse findById(Long id) {
        return productService.findById(id)
                .map(ProductMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PRODUTO_NAO_ENCONTRADO, Status.NOT_FOUND));
    }
}
