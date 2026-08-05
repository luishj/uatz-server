package br.com.uatz.server.service;

/**
 * Transporte das mensagens enviadas ao cliente. Existe para que o fluxo de
 * negócio (envio das opções e fechamento do pedido) não dependa da WhatsApp
 * Cloud API: a implementação atual apenas registra a mensagem, e a integração
 * real entra trocando o bean, sem tocar nos serviços.
 */
public interface WhatsAppGateway {

    /**
     * Envia o texto para o telefone informado. Quem chama é responsável por
     * gravar a mensagem na conversa do cliente.
     */
    public abstract void sendMessage(String phone, String text);

}
