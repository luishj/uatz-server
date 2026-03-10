package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.client.ClientRequest;
import br.com.uatz.api.dto.client.ClientResponse;
import br.com.uatz.model.entity.Client;
import java.time.LocalDateTime;

public final class ClientApiMapper {

    private ClientApiMapper() {
    }

    public static Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setPhone(request.phone());
        client.setCity(request.city());
        client.setState(request.state());
        client.setCreatedAt(LocalDateTime.now());
        return client;
    }

    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getPhone(),
                client.getCity(),
                client.getState()
        );
    }
}

