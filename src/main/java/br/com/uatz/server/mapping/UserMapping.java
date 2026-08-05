package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.user.UserResponse;
import br.com.uatz.model.User;

public final class UserMapping {

    private UserMapping() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}

