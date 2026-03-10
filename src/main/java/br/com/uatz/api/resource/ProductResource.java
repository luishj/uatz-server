package br.com.uatz.api.resource;

import br.com.uatz.api.dto.product.ProductRequest;
import br.com.uatz.api.dto.product.ProductResponse;
import br.com.uatz.api.mapper.ProductApiMapper;
import br.com.uatz.service.ProductService;
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

@Path("/api/products")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @POST
    public Response create(@Valid ProductRequest request) {
        ProductResponse response = ProductApiMapper.toResponse(productService.create(request));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }


    @GET
    public List<ProductResponse> findAll() {
        return productService.findAll()
                .stream()
                .map(ProductApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public ProductResponse findById(@PathParam("id") Long id) {
        return productService.findById(id)
                .map(ProductApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("Product not found", Response.Status.NOT_FOUND));
    }
}
