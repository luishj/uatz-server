package br.com.uatz.server.dto.auth;

import br.com.uatz.model.enumerador.UserRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        Long userId,
        String name,
        String email,
        UserRole role
) {
}

