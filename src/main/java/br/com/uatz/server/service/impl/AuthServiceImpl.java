package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.auth.AuthBootstrapRequest;
import br.com.uatz.server.dto.auth.AuthLoginRequest;
import br.com.uatz.server.dto.auth.AuthResponse;
import br.com.uatz.server.dto.user.UserCreateRequest;
import br.com.uatz.model.User;
import br.com.uatz.model.enumerador.UserRole;
import br.com.uatz.server.service.AuthService;
import br.com.uatz.server.service.PasswordService;
import br.com.uatz.server.service.TokenService;
import br.com.uatz.server.service.UserService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response.Status;

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
            throw MessageBuilder.build(CloudMessage.BOOTSTRAP_INDISPONIVEL, Status.CONFLICT);
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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.CREDENCIAIS_INVALIDAS, Status.UNAUTHORIZED));

        if (!passwordService.matches(request.password(), user.getPasswordHash())) {
            throw MessageBuilder.build(CloudMessage.CREDENCIAIS_INVALIDAS, Status.UNAUTHORIZED);
        }

        return tokenService.generate(user);
    }
}

