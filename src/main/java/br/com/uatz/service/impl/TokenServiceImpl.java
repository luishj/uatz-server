package br.com.uatz.service.impl;

import br.com.uatz.api.dto.auth.AuthResponse;
import br.com.uatz.model.entity.User;
import br.com.uatz.service.TokenService;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenServiceImpl implements TokenService {

    private static final long EXPIRES_IN_SECONDS = 3600L;

    @Override
    public AuthResponse generate(User user) {
        String token = Jwt.issuer("uatz")
                .upn(user.getEmail())
                .subject(String.valueOf(user.getId()))
                .groups(Set.of(user.getRole().name()))
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .expiresIn(Duration.ofSeconds(EXPIRES_IN_SECONDS))
                .sign();

        return new AuthResponse(
                token,
                "Bearer",
                EXPIRES_IN_SECONDS,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}

