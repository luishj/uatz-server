package br.com.uatz.api.exception;

import br.com.uatz.api.dto.common.ApiErrorResponse;
import br.com.uatz.api.dto.common.ApiFieldErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ConstraintViolationException validationException) {
            return buildValidationResponse(validationException);
        }

        if (exception instanceof WebApplicationException webApplicationException) {
            return buildWebApplicationResponse(webApplicationException);
        }

        ApiErrorResponse response = new ApiErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal server error",
                LocalDateTime.now(),
                List.of()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }

    private Response buildValidationResponse(ConstraintViolationException exception) {
        List<ApiFieldErrorResponse> errors = exception.getConstraintViolations()
                .stream()
                .sorted(Comparator.comparing(this::extractFieldName))
                .map(violation -> new ApiFieldErrorResponse(extractFieldName(violation), violation.getMessage()))
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }

    private Response buildWebApplicationResponse(WebApplicationException exception) {
        String message = exception.getMessage() != null && !exception.getMessage().isBlank()
                ? exception.getMessage()
                : Response.Status.fromStatusCode(exception.getResponse().getStatus()).getReasonPhrase();

        ApiErrorResponse response = new ApiErrorResponse(
                exception.getResponse().getStatus(),
                message,
                LocalDateTime.now(),
                List.of()
        );

        return Response.status(exception.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int index = path.lastIndexOf('.');
        return index >= 0 ? path.substring(index + 1) : path;
    }
}
