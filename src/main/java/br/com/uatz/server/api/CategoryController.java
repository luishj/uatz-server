package br.com.uatz.server.api;

import br.com.uatz.server.dto.category.CategoryRequest;
import br.com.uatz.server.dto.category.CategoryResponse;
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

@Path("/api/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface CategoryController {

    @POST
    public abstract Response create(@Valid CategoryRequest request);

    @GET
    public abstract List<CategoryResponse> findAll();

    @GET
    @Path("/{id}")
    public abstract CategoryResponse findById(@PathParam("id") Long id);
}
