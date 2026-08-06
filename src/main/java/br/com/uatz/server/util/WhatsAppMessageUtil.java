package br.com.uatz.server.util;

import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Textos e links de WhatsApp usados no fechamento do pedido. O link é o formato
 * público {@code https://wa.me/<telefone>?text=<mensagem>}, que abre uma conversa
 * nova já com o texto digitado.
 */
public final class WhatsAppMessageUtil {

	private static final String LINK_BASE = "https://wa.me/";
	private static final String MARCA = "UATZ";
	private static final Locale LOCALE_BRASIL = Locale.forLanguageTag("pt-BR");
	private static final String QUEBRA_LINHA = "\n";

	private WhatsAppMessageUtil() {
	}

	/**
	 * Lista numerada enviada ao cliente. É o número desta lista que ele responde.
	 */
	public static String montarMensagemOpcoes(Long codigoPedido, List<BudgetRequestQuoteOptionResponse> opcoes) {
		StringBuilder texto = new StringBuilder();
		texto.append("Orçamentos do seu pedido #").append(codigoPedido).append(":").append(QUEBRA_LINHA);

		for (BudgetRequestQuoteOptionResponse opcao : opcoes) {
			texto.append(QUEBRA_LINHA)
					.append(opcao.optionNumber()).append(") ")
					.append(opcao.vendorName()).append(" - ")
					.append(formatarValor(opcao.totalPrice()));
		}

		texto.append(QUEBRA_LINHA).append(QUEBRA_LINHA)
				.append("Responda com o número da opção escolhida.");

		return texto.toString();
	}

	/**
	 * Mensagem automática enviada ao cliente depois da escolha, com o link que
	 * abre a conversa com o vendedor.
	 */
	public static String montarMensagemEscolha(Long codigoPedido, String nomeFornecedor, BigDecimal total, String linkVendedor) {
		StringBuilder texto = new StringBuilder()
				.append("Pedido #").append(codigoPedido).append(" fechado com ").append(nomeFornecedor)
				.append(" por ").append(formatarValor(total)).append(".");

		if (!StringUtil.isNullOrEmpty(linkVendedor)) {
			texto.append(QUEBRA_LINHA).append(QUEBRA_LINHA)
					.append("Clique no link abaixo e fale direto com o vendedor:").append(QUEBRA_LINHA)
					.append(linkVendedor);
		}

		return texto.toString();
	}

	/**
	 * Texto que já vai digitado na conversa que o cliente abre com o vendedor.
	 */
	public static String montarTextoClienteParaVendedor(Long codigoPedido, List<String> produtos, BigDecimal total) {
		return new StringBuilder()
				.append("Olá! Escolhi sua cotação na ").append(MARCA)
				.append(" para o pedido #").append(codigoPedido)
				.append(" dos seguintes produtos: ").append(String.join(", ", produtos)).append(".")
				.append(" Total: ").append(formatarValor(total)).append(".")
				.toString();
	}

	/**
	 * Resposta ao cliente que respondeu um número que não existe na lista de
	 * opções enviada.
	 */
	public static String montarMensagemOpcaoInvalida(Long codigoPedido, Integer opcao) {
		return new StringBuilder()
				.append("Não encontrei a opção ").append(opcao)
				.append(" no pedido #").append(codigoPedido).append(".")
				.append(QUEBRA_LINHA)
				.append("Responda com o número de uma das opções que enviamos.")
				.toString();
	}

	/**
	 * Texto que o fornecedor usa quando é ele quem inicia a conversa.
	 */
	public static String montarTextoFornecedorParaCliente(Long codigoPedido, String nomeFornecedor) {
		return new StringBuilder()
				.append("Olá! Sou da ").append(nomeFornecedor)
				.append(". Você escolheu nossa cotação na ").append(MARCA)
				.append(" para o pedido #").append(codigoPedido).append(".")
				.toString();
	}

	/**
	 * Monta o link de conversa nova. Retorna nulo quando não há telefone.
	 */
	public static String montarLink(String telefone, String texto) {
		String numero = somenteDigitos(telefone);

		if (StringUtil.isNullOrEmpty(numero)) {
			return null;
		}

		return LINK_BASE + numero + "?text=" + URLEncoder.encode(StringUtil.naoNulo(texto), StandardCharsets.UTF_8);
	}

	/**
	 * Descrição do item como ela aparece na mensagem: "10 saco de Cimento CP2".
	 */
	public static String montarDescricaoItem(BigDecimal quantidade, String unidade, String nomeProduto) {
		StringBuilder descricao = new StringBuilder(formatarQuantidade(quantidade));

		if (!StringUtil.isNullOrEmpty(unidade)) {
			descricao.append(" ").append(unidade);
		}

		return descricao.append(" de ").append(nomeProduto).toString();
	}

	public static String formatarValor(BigDecimal valor) {
		if (valor == null) {
			return "-";
		}

		return NumberFormat.getCurrencyInstance(LOCALE_BRASIL).format(valor);
	}

	private static String formatarQuantidade(BigDecimal quantidade) {
		if (quantidade == null) {
			return StringUtil.STRING_VAZIA;
		}

		return quantidade.stripTrailingZeros().toPlainString();
	}

	/**
	 * Telefone no formato aceito pela Cloud API e pelo link wa.me: só dígitos,
	 * já com o código do país.
	 */
	public static String somenteDigitos(String telefone) {
		if (StringUtil.isNullOrEmpty(telefone)) {
			return StringUtil.STRING_VAZIA;
		}

		return telefone.replaceAll("\\D", StringUtil.STRING_VAZIA);
	}

}
