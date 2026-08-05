package br.com.uatz.server.api;

import br.com.uatz.server.dto.product.ProductRequest;
import br.com.uatz.server.dto.product.ProductResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface ProductController {

    @POST
    public abstract Response create(@Valid ProductRequest request);

    @GET
    public abstract List<ProductResponse> findAll();

    @GET
    @Path("/{id}")
    public abstract ProductResponse findById(@PathParam("id") Long id);
}
