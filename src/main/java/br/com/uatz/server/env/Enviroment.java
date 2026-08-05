package br.com.uatz.server.env;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Configurações de ambiente da aplicação, injetáveis nos beans.
 */
@ApplicationScoped
public class Enviroment {

	@ConfigProperty(name = "quarkus.application.name")
	private String nomeApp;

	@ConfigProperty(name = "versao.app")
	private String versaoApp;

	@ConfigProperty(name = "quarkus.datasource.jdbc.url")
	private String urlDatabase;

	public String getNomeApp() {
		return nomeApp;
	}

	public String getVersaoApp() {
		return versaoApp;
	}

	public String getUrlDatabase() {
		return urlDatabase;
	}

}
