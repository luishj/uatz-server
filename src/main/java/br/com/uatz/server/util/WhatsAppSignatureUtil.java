package br.com.uatz.server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Validação do header {@code X-Hub-Signature-256} que a Meta envia no webhook.
 * A assinatura é o HMAC-SHA256 do corpo <b>bruto</b> da requisição usando o app
 * secret como chave, no formato {@code sha256=<hex>} — por isso o controller
 * recebe o payload como texto e não como DTO: qualquer reserialização mudaria
 * os bytes e invalidaria a comparação.
 */
public final class WhatsAppSignatureUtil {

	private static final String ALGORITMO_HMAC = "HmacSHA256";
	private static final String PREFIXO_ASSINATURA = "sha256=";

	private WhatsAppSignatureUtil() {
	}

	/**
	 * Compara a assinatura recebida com a calculada sobre o corpo. A comparação
	 * é feita em tempo constante para não vazar o segredo por temporização.
	 */
	public static boolean assinaturaValida(String segredoApp, String corpo, String assinaturaRecebida) {
		if (StringUtil.isNullOrEmpty(segredoApp) || StringUtil.isNullOrEmpty(assinaturaRecebida)) {
			return false;
		}

		if (!assinaturaRecebida.startsWith(PREFIXO_ASSINATURA)) {
			return false;
		}

		byte[] esperada = calcularHmac(segredoApp, StringUtil.naoNulo(corpo));
		byte[] recebida = decodificarHex(assinaturaRecebida.substring(PREFIXO_ASSINATURA.length()));

		return MessageDigest.isEqual(esperada, recebida);
	}

	private static byte[] calcularHmac(String segredoApp, String corpo) {
		try {
			Mac mac = Mac.getInstance(ALGORITMO_HMAC);
			mac.init(new SecretKeySpec(segredoApp.getBytes(StandardCharsets.UTF_8), ALGORITMO_HMAC));
			return mac.doFinal(corpo.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new IllegalStateException("Falha ao calcular a assinatura do webhook", e);
		}
	}

	/**
	 * Devolve um array vazio quando o hex é inválido: a comparação simplesmente
	 * falha, sem expor o motivo a quem chamou o webhook.
	 */
	private static byte[] decodificarHex(String hex) {
		try {
			return HexFormat.of().parseHex(hex);
		} catch (IllegalArgumentException e) {
			return new byte[0];
		}
	}

}
