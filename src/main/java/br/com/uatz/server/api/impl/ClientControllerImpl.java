package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.ClientController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.client.ClientRequest;
import br.com.uatz.server.dto.client.ClientResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.ClientMapping;
import br.com.uatz.server.service.ClientService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class ClientControllerImpl implements ClientController {

    @Inject
    ClientService clientService;

    @Override
    public Response create(ClientRequest request) {
        ClientResponse response = ClientMapping.toResponse(clientService.save(ClientMapping.toEntity(request)));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    @RolesAllowed(Perfil.ADMIN)
    public List<ClientResponse> findAll() {
        return clientService.findAll()
                .stream()
                .map(ClientMapping::toResponse)
                .toList();
    }

    @Override
    public ClientResponse findById(Long id) {
        return clientService.findById(id)
                .map(ClientMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.CLIENTE_NAO_ENCONTRADO, Status.NOT_FOUND));
    }
}
