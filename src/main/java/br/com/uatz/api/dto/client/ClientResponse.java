package br.com.uatz.api.dto.client;

public record ClientResponse(
        Long id,
        String phone,
        String city,
        String state
) {
}

