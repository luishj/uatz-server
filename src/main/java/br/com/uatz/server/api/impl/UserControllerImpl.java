package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.UserController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.user.UserCreateRequest;
import br.com.uatz.server.dto.user.UserResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.UserMapping;
import br.com.uatz.server.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@RolesAllowed(Perfil.ADMIN)
public class UserControllerImpl implements UserController {

    @Inject
    UserService userService;

    @Override
    public Response create(UserCreateRequest request) {
        UserResponse response = UserMapping.toResponse(userService.create(request));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    public List<UserResponse> findAll() {
        return userService.findAll()
                .stream()
                .map(UserMapping::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return userService.findById(id)
                .map(UserMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.USUARIO_NAO_ENCONTRADO, Status.NOT_FOUND));
    }
}
