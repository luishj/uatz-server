package br.com.uatz.server.repository;

import br.com.uatz.model.Conversation;
import java.util.Optional;

public interface ConversationRepository extends GenericRepository<Conversation, Long> {

    Conversation save(Conversation conversation);

    Optional<Conversation> findLastByClientId(Long clientId);
}
