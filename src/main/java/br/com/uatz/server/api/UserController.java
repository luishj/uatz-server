package br.com.uatz.server.api;

import br.com.uatz.server.dto.user.UserCreateRequest;
import br.com.uatz.server.dto.user.UserResponse;
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

@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface UserController {

    @POST
    public abstract Response create(@Valid UserCreateRequest request);

    @GET
    public abstract List<UserResponse> findAll();

    @GET
    @Path("/{id}")
    public abstract UserResponse findById(@PathParam("id") Long id);
}
