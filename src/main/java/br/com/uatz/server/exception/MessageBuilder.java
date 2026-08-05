package br.com.uatz.server.exception;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Fábrica das exceções de negócio. A mensagem vem de `messages_pt_BR.properties`
 * usando o nome da {@link CloudMessage} como chave, e o status HTTP é o que o
 * {@link ErrorMapper} devolve na resposta.
 *
 * <pre>
 * throw MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND);
 *
 * // com substituição de parâmetros:
 * throw MessageBuilder.build(CloudMessage.FORNECEDOR_INATIVO, Status.CONFLICT, GerarMap.gerarMapString("{0}", nome));
 * </pre>
 */
public final class MessageBuilder {

	private MessageBuilder() {
	}

	/**
	 * Constrói a exceção com status {@link Status#BAD_REQUEST}.
	 */
	public static WebApplicationException build(CloudMessage cloudMessage, Object... params) {
		return build(cloudMessage, Status.BAD_REQUEST, params);
	}

	public static WebApplicationException build(CloudMessage cloudMessage, Status status, Object... params) {

		String message = getMessage(cloudMessage, params);

		BusinessServerException businessServerException = new BusinessServerException(cloudMessage.name(), status.getStatusCode(), message, message);

		return new WebApplicationException(businessServerException, status);
	}

	public static String getMessage(CloudMessage cloudMessage, Object... params) {
		return getMessage(cloudMessage.name(), params);
	}

	public static String getMessage(String name, Object... params) {

		ResourceBundle bundle = getResourceBundle();

		String message = getString(name, bundle);

		return formatString(message, params);
	}

	private static ResourceBundle getResourceBundle() {

		try {
			return ResourceBundle.getBundle("messages", Locale.of("pt", "BR"));
		} catch (MissingResourceException e) {
			throw new WebApplicationException("ARQUIVO DE MENSAGENS NÃO ENCONTRADO. POR FAVOR, VERIFIQUE.", Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	private static String getString(String name, ResourceBundle bundle) {

		try {
			return bundle.getString(name);
		} catch (MissingResourceException e) {
			return "ATENÇÃO! MENSAGEM NÃO ENCONTRADA PARA CHAVE (" + name + ").";
		}
	}

	@SuppressWarnings("unchecked")
	private static String formatString(String message, Object... params) {

		if (params == null) {
			return message;
		}

		for (Object object : params) {

			if (!(object instanceof Map)) {
				continue;
			}

			for (Entry<String, String> entry : ((Map<String, String>) object).entrySet()) {
				message = message.replace(entry.getKey(), entry.getValue());
			}
		}

		return message;
	}

}
