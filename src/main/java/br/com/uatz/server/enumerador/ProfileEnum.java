package br.com.uatz.server.enumerador;

import java.util.HashMap;
import java.util.Map;

public enum ProfileEnum {

	PROD("prod"), DEV("dev"), TEST("test");

	private String identificador;

	private static final Map<String, ProfileEnum> TYPES_BY_VALUE = new HashMap<>();

	static {
		for (ProfileEnum type : ProfileEnum.values()) {
			TYPES_BY_VALUE.put(type.identificador, type);
		}
	}

	private ProfileEnum(String identificador) {
		this.identificador = identificador;
	}

	/**
	 * Retorna o profile do identificador informado, assumindo {@link #PROD} quando
	 * desconhecido (comportamento mais restritivo).
	 */
	public static ProfileEnum forValue(String value) {
		return TYPES_BY_VALUE.getOrDefault(value, PROD);
	}

	public String getIdentificador() {
		return identificador;
	}

}
