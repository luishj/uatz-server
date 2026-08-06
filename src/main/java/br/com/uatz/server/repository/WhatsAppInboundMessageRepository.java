package br.com.uatz.server.repository;

import br.com.uatz.model.WhatsAppInboundMessage;

public interface WhatsAppInboundMessageRepository extends GenericRepository<WhatsAppInboundMessage, Long> {

    /**
     * Tenta registrar a mensagem como recebida numa transação própria. Retorna
     * {@code true} quando a linha foi inserida (primeira vez que a mensagem
     * aparece) e {@code false} quando a unique constraint estourou, ou seja, a
     * mensagem já foi processada — é o reenvio da Meta que precisa ser ignorado.
     */
    boolean claim(String messageId);
}
