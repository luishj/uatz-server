package br.com.uatz.api.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<ApiFieldErrorResponse> errors
) {
}

