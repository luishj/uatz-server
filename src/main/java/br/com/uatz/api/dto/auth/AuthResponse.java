package br.com.uatz.api.dto.auth;

import br.com.uatz.model.enums.UserRole;

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

