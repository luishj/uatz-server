package br.com.uatz.server.repository.impl;

import br.com.uatz.model.Conversation;
import br.com.uatz.server.repository.ConversationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConversationRepositoryImpl extends GenericRepositoryImpl<Conversation, Long> implements ConversationRepository {

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        persist(conversation);
        return conversation;
    }
}
