package br.com.uatz.api.resource;

import br.com.uatz.api.dto.auth.AuthBootstrapRequest;
import br.com.uatz.api.dto.auth.AuthLoginRequest;
import br.com.uatz.api.dto.auth.AuthResponse;
import br.com.uatz.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/bootstrap")
    public Response bootstrap(@Valid AuthBootstrapRequest request) {
        AuthResponse response = authService.bootstrapAdmin(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/login")
    public AuthResponse login(@Valid AuthLoginRequest request) {
        return authService.login(request);
    }
}

