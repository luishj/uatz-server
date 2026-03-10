package br.com.uatz.api.resource;

import br.com.uatz.api.dto.category.CategoryRequest;
import br.com.uatz.api.dto.category.CategoryResponse;
import br.com.uatz.api.mapper.CategoryApiMapper;
import br.com.uatz.service.CategoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/categories")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CategoryService categoryService;

    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @POST
    public Response create(@Valid CategoryRequest request) {
        CategoryResponse response = CategoryApiMapper.toResponse(categoryService.create(request));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<CategoryResponse> findAll() {
        return categoryService.findAll()
                .stream()
                .map(CategoryApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public CategoryResponse findById(@PathParam("id") Long id) {
        return categoryService.findById(id)
                .map(CategoryApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("Category not found", Response.Status.NOT_FOUND));
    }
}
