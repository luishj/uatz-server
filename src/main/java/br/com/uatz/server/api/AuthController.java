package br.com.uatz.server.api;

import br.com.uatz.server.dto.auth.AuthBootstrapRequest;
import br.com.uatz.server.dto.auth.AuthLoginRequest;
import br.com.uatz.server.dto.auth.AuthResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface AuthController {

    @POST
    @Path("/bootstrap")
    public abstract Response bootstrap(@Valid AuthBootstrapRequest request);

    @POST
    @Path("/login")
    public abstract AuthResponse login(@Valid AuthLoginRequest request);
}
