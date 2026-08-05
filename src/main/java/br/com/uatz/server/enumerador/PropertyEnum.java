package br.com.uatz.server.enumerador;

public enum PropertyEnum {

	/* @formatter:off */

	VERSAO("versao.app"),
	PROFILE("quarkus.profile"),
	URL_DATABASE("quarkus.datasource.jdbc.url");

	/* @formatter:on */

	private String nome;

	private PropertyEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
