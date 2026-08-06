package br.com.uatz.server.service;

import br.com.uatz.model.Client;
import br.com.uatz.model.Conversation;
import br.com.uatz.model.Message;

/**
 * Histórico de WhatsApp do cliente. Centraliza a conversa e o registro das
 * mensagens para que o webhook e a criação do pedido gravem do mesmo jeito.
 */
public interface ConversationService {

    /**
     * Última conversa do cliente, criando uma nova quando ele ainda não tem
     * nenhuma.
     */
    Conversation resolveConversation(Client client);

    Message registerInbound(Conversation conversation, String text);

    Message registerOutbound(Conversation conversation, String text);
}
