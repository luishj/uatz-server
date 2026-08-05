package br.com.uatz.server.dto.common;

public record ApiFieldErrorResponse(
        String field,
        String message
) {
}

