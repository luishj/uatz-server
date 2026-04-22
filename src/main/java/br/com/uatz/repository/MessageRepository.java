package br.com.uatz.repository;

import br.com.uatz.model.entity.Message;

public interface MessageRepository {

    Message save(Message message);
}
