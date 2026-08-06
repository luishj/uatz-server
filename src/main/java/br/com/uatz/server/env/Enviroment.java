package br.com.uatz.server.env;

import java.util.Optional;

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

	/**
	 * Liga o envio pela WhatsApp Cloud API. Desligado, as mensagens só vão para
	 * o log — é o modo usado em desenvolvimento, sem número verificado.
	 */
	@ConfigProperty(name = "whatsapp.enabled")
	private boolean whatsAppHabilitado;

	/**
	 * Token combinado com a Meta na configuração do webhook: ela o devolve na
	 * chamada de verificação e o valor precisa bater.
	 */
	@ConfigProperty(name = "whatsapp.verify-token")
	private Optional<String> whatsAppTokenVerificacao;

	/**
	 * App secret usado para conferir a assinatura das notificações recebidas.
	 * Vazio desativa a conferência (apenas para desenvolvimento).
	 */
	@ConfigProperty(name = "whatsapp.app-secret")
	private Optional<String> whatsAppSegredoApp;

	@ConfigProperty(name = "whatsapp.access-token")
	private Optional<String> whatsAppTokenAcesso;

	@ConfigProperty(name = "whatsapp.phone-number-id")
	private Optional<String> whatsAppPhoneNumberId;

	public String getNomeApp() {
		return nomeApp;
	}

	public String getVersaoApp() {
		return versaoApp;
	}

	public String getUrlDatabase() {
		return urlDatabase;
	}

	public boolean isWhatsAppHabilitado() {
		return whatsAppHabilitado;
	}

	public String getWhatsAppTokenVerificacao() {
		return whatsAppTokenVerificacao.orElse(null);
	}

	public String getWhatsAppSegredoApp() {
		return whatsAppSegredoApp.orElse(null);
	}

	public String getWhatsAppTokenAcesso() {
		return whatsAppTokenAcesso.orElse(null);
	}

	public String getWhatsAppPhoneNumberId() {
		return whatsAppPhoneNumberId.orElse(null);
	}

}
