package br.com.uatz.server.repository;

import br.com.uatz.model.Message;

public interface MessageRepository extends GenericRepository<Message, Long> {

    Message save(Message message);
}
