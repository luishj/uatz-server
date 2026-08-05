package br.com.uatz.server.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Exceção lançada quando ocorre uma falha de negócio. Não é extensível e só deve
 * ser instanciada pelo {@link MessageBuilder}, que resolve a mensagem a partir do
 * resource bundle.
 */
@RegisterForReflection
public final class BusinessServerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Nome da propriedade do resource da mensagem.
	 */
	private final String codigo;

	/**
	 * Texto da mensagem apresentada ao usuário.
	 */
	private final String mensagem;

	/**
	 * Status HTTP que deve ser devolvido na resposta.
	 */
	private final int status;

	BusinessServerException(String codigo, int status, String mensagemTecnica, String mensagem) {

		super(mensagemTecnica);

		this.codigo = codigo;
		this.status = status;
		this.mensagem = mensagem;
	}

	BusinessServerException(String codigo, int status, String mensagemTecnica, String mensagem, Throwable throwable) {

		super(mensagemTecnica, throwable);

		this.codigo = codigo;
		this.status = status;
		this.mensagem = mensagem;
	}

	/**
	 * Retorna o código de erro (nome da chave em {@link CloudMessage}).
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Retorna o status HTTP da resposta.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Retorna o texto da mensagem ao usuário.
	 */
	public String getMensagem() {
		return mensagem;
	}

	/**
	 * Retorna o texto da mensagem técnica.
	 */
	public String getMensagemTecnica() {
		return super.getMessage();
	}

}
