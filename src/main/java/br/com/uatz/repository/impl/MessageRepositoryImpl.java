package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.Message;
import br.com.uatz.repository.MessageRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository, PanacheRepositoryBase<Message, Long> {

    @Override
    @Transactional
    public Message save(Message message) {
        persist(message);
        return message;
    }
}
