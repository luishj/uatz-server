package br.com.uatz.service.impl;

import br.com.uatz.api.dto.auth.AuthBootstrapRequest;
import br.com.uatz.api.dto.auth.AuthLoginRequest;
import br.com.uatz.api.dto.auth.AuthResponse;
import br.com.uatz.api.dto.user.UserCreateRequest;
import br.com.uatz.model.entity.User;
import br.com.uatz.model.enums.UserRole;
import br.com.uatz.service.AuthService;
import br.com.uatz.service.PasswordService;
import br.com.uatz.service.TokenService;
import br.com.uatz.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthServiceImpl(UserService userService, PasswordService passwordService, TokenService tokenService) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponse bootstrapAdmin(AuthBootstrapRequest request) {
        if (userService.countAll() > 0) {
            throw new WebApplicationException("Bootstrap is no longer available", Response.Status.CONFLICT);
        }

        User user = userService.create(new UserCreateRequest(
                request.name(),
                request.email(),
                request.password(),
                UserRole.ADMIN
        ));

        return tokenService.generate(user);
    }

    @Override
    public AuthResponse login(AuthLoginRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED));

        if (!passwordService.matches(request.password(), user.getPasswordHash())) {
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }

        return tokenService.generate(user);
    }
}

