package br.com.uatz.server.util;

/**
 * Utilitários de String, com apenas o que o projeto usa. Atenção:
 * {@link #isNullOrEmpty(String)} também considera vazia uma string composta
 * somente por espaços.
 */
public final class StringUtil {

	public static final String STRING_VAZIA = "";

	private StringUtil() {
	}

	/**
	 * Retorna verdadeiro quando o texto é nulo, vazio ou contém apenas espaços.
	 */
	public static boolean isNullOrEmpty(String texto) {
		return texto == null || texto.isBlank();
	}

	/**
	 * Retorna o próprio texto ou {@link #STRING_VAZIA} quando ele for nulo.
	 */
	public static String naoNulo(String texto) {
		return texto == null ? STRING_VAZIA : texto;
	}

}
