package br.com.uatz.server.dto.vendor;

public record VendorResponse(
        Long id,
        String name,
        String phone,
        String email,
        String city,
        String state,
        Boolean active
) {
}

