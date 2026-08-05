package br.com.uatz.server.util;

import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

import br.com.uatz.server.enumerador.ProfileEnum;
import br.com.uatz.server.enumerador.PropertyEnum;
import io.quarkus.runtime.LaunchMode;

/**
 * Leitura de propriedades de configuração fora de um bean CDI.
 */
public final class PropertyUtil {

	private PropertyUtil() {
	}

	public static String getProperty(PropertyEnum property) {
		return getProperty(property, null);
	}

	public static String getProperty(PropertyEnum property, String defaultValue) {
		Optional<String> valor = ConfigProvider.getConfig().getOptionalValue(property.getNome(), String.class);
		return valor.orElse(defaultValue);
	}

	public static ProfileEnum getProfile() {
		return ProfileEnum.forValue(getProperty(PropertyEnum.PROFILE, LaunchMode.current().getDefaultProfile()));
	}

	public static Boolean isProdProfile() {
		return getProfile().equals(ProfileEnum.PROD);
	}

}
