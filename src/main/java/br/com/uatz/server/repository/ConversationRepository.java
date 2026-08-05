package br.com.uatz.server.repository;

import br.com.uatz.model.Conversation;

public interface ConversationRepository extends GenericRepository<Conversation, Long> {

    Conversation save(Conversation conversation);
}
