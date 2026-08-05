package br.com.uatz.server.dto.client;

public record ClientResponse(
        Long id,
        String phone,
        String city,
        String state
) {
}

