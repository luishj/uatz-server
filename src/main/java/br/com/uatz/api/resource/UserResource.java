package br.com.uatz.api.resource;

import br.com.uatz.api.dto.user.UserCreateRequest;
import br.com.uatz.api.dto.user.UserResponse;
import br.com.uatz.api.mapper.UserApiMapper;
import br.com.uatz.service.UserService;
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

@Path("/api/users")
@RolesAllowed("ADMIN")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    public Response create(@Valid UserCreateRequest request) {
        UserResponse response = UserApiMapper.toResponse(userService.create(request));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<UserResponse> findAll() {
        return userService.findAll()
                .stream()
                .map(UserApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public UserResponse findById(@PathParam("id") Long id) {
        return userService.findById(id)
                .map(UserApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("User not found", Response.Status.NOT_FOUND));
    }
}

