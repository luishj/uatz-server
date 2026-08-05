package br.com.uatz.server.constante;

/**
 * Códigos dos perfis de acesso, iguais aos registrados na tabela `roles`. São
 * constantes de compilação para poderem ser usadas em {@code @RolesAllowed}.
 */
public final class Perfil {

	public static final String ADMIN = "ADMIN";

	public static final String OPERATOR = "OPERATOR";

	public static final String VENDOR = "VENDOR";

	private Perfil() {
	}

}
