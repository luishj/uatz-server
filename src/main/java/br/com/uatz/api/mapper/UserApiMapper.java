package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.user.UserResponse;
import br.com.uatz.model.entity.User;

public final class UserApiMapper {

    private UserApiMapper() {
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

