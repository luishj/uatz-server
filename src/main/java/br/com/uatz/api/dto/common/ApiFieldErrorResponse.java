package br.com.uatz.api.dto.common;

public record ApiFieldErrorResponse(
        String field,
        String message
) {
}

