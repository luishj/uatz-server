package br.com.uatz.api.dto.user;

import br.com.uatz.model.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}

