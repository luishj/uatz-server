package br.com.uatz.server.exception;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Logger;

import br.com.uatz.server.dto.common.ApiErrorResponse;
import br.com.uatz.server.dto.common.ApiFieldErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Tratamento global de erros. Serializa qualquer falha no formato
 * {@link ApiErrorResponse}.
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

	private static final Logger logger = Logger.getLogger(ErrorMapper.class);

	@Override
	public Response toResponse(Exception exception) {

		if (exception instanceof ConstraintViolationException validationException) {
			return buildValidationResponse(validationException);
		}

		if (exception.getCause() instanceof BusinessServerException businessServerException) {
			return buildBusinessResponse(businessServerException);
		}

		if (exception instanceof WebApplicationException webApplicationException) {
			return buildWebApplicationResponse(webApplicationException);
		}

		logger.fatal("ERROR", exception);

		return build(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal server error", List.of());
	}

	private Response buildBusinessResponse(BusinessServerException exception) {

		logger.debugf("Falha de negócio [%s]: %s", exception.getCodigo(), exception.getMensagemTecnica());

		return build(exception.getStatus(), exception.getMensagem(), List.of());
	}

	private Response buildValidationResponse(ConstraintViolationException exception) {

		List<ApiFieldErrorResponse> errors = exception.getConstraintViolations()
				.stream()
				.sorted(Comparator.comparing(this::extractFieldName))
				.map(violation -> new ApiFieldErrorResponse(extractFieldName(violation), violation.getMessage()))
				.toList();

		return build(Response.Status.BAD_REQUEST.getStatusCode(), "Validation failed", errors);
	}

	private Response buildWebApplicationResponse(WebApplicationException exception) {

		String message = exception.getMessage() != null && !exception.getMessage().isBlank()
				? exception.getMessage()
				: Response.Status.fromStatusCode(exception.getResponse().getStatus()).getReasonPhrase();

		return build(exception.getResponse().getStatus(), message, List.of());
	}

	private Response build(int status, String message, List<ApiFieldErrorResponse> errors) {

		ApiErrorResponse response = new ApiErrorResponse(status, message, LocalDateTime.now(), errors);

		return Response.status(status)
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
