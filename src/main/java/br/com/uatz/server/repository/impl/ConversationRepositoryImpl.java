package br.com.uatz.server.repository.impl;

import br.com.uatz.model.Conversation;
import br.com.uatz.server.repository.ConversationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class ConversationRepositoryImpl extends GenericRepositoryImpl<Conversation, Long> implements ConversationRepository {

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        persist(conversation);
        return conversation;
    }

    @Override
    public Optional<Conversation> findLastByClientId(Long clientId) {
        return find("client.id = ?1 order by id desc", clientId).firstResultOptional();
    }
}
