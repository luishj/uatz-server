package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.client.ClientRequest;
import br.com.uatz.server.dto.client.ClientResponse;
import br.com.uatz.model.Client;
import java.time.LocalDateTime;

public final class ClientMapping {

    private ClientMapping() {
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

