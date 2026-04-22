package br.com.uatz.repository;

import br.com.uatz.model.entity.Conversation;

public interface ConversationRepository {

    Conversation save(Conversation conversation);
}
