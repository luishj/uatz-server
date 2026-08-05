package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.AuthController;
import br.com.uatz.server.dto.auth.AuthBootstrapRequest;
import br.com.uatz.server.dto.auth.AuthLoginRequest;
import br.com.uatz.server.dto.auth.AuthResponse;
import br.com.uatz.server.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@PermitAll
public class AuthControllerImpl implements AuthController {

    @Inject
    AuthService authService;

    @Override
    public Response bootstrap(AuthBootstrapRequest request) {
        AuthResponse response = authService.bootstrapAdmin(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Override
    public AuthResponse login(AuthLoginRequest request) {
        return authService.login(request);
    }
}
