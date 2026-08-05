package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.CategoryController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.category.CategoryRequest;
import br.com.uatz.server.dto.category.CategoryResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.CategoryMapping;
import br.com.uatz.server.service.CategoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class CategoryControllerImpl implements CategoryController {

    @Inject
    CategoryService categoryService;

    @Override
    public Response create(CategoryRequest request) {
        CategoryResponse response = CategoryMapping.toResponse(categoryService.create(request));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryService.findAll()
                .stream()
                .map(CategoryMapping::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(Long id) {
        return categoryService.findById(id)
                .map(CategoryMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.CATEGORIA_NAO_ENCONTRADA, Status.NOT_FOUND));
    }
}
