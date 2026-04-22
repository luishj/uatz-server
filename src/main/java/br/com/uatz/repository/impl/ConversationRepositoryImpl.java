package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.Conversation;
import br.com.uatz.repository.ConversationRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConversationRepositoryImpl implements ConversationRepository, PanacheRepositoryBase<Conversation, Long> {

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        persist(conversation);
        return conversation;
    }
}
