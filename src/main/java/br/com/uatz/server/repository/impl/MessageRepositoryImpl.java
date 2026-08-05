package br.com.uatz.server.repository.impl;

import br.com.uatz.model.Message;
import br.com.uatz.server.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MessageRepositoryImpl extends GenericRepositoryImpl<Message, Long> implements MessageRepository {

    @Override
    @Transactional
    public Message save(Message message) {
        persist(message);
        return message;
    }
}
