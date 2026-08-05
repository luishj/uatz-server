package br.com.uatz.server.dto.user;

import br.com.uatz.model.enumerador.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}

