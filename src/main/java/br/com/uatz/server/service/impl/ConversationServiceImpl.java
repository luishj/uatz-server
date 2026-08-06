package br.com.uatz.server.service.impl;

import br.com.uatz.model.Client;
import br.com.uatz.model.Conversation;
import br.com.uatz.model.Message;
import br.com.uatz.model.enumerador.MessageDirection;
import br.com.uatz.server.repository.ConversationRepository;
import br.com.uatz.server.repository.MessageRepository;
import br.com.uatz.server.service.ConversationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class ConversationServiceImpl implements ConversationService {

    private static final String CANAL_WHATSAPP = "WHATSAPP";

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationServiceImpl(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public Conversation resolveConversation(Client client) {
        return conversationRepository.findLastByClientId(client.getId())
                .orElseGet(() -> createConversation(client));
    }

    @Override
    @Transactional
    public Message registerInbound(Conversation conversation, String text) {
        return registerMessage(conversation, MessageDirection.IN, text);
    }

    @Override
    @Transactional
    public Message registerOutbound(Conversation conversation, String text) {
        return registerMessage(conversation, MessageDirection.OUT, text);
    }

    private Message registerMessage(Conversation conversation, MessageDirection direction, String text) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setDirection(direction);
        message.setMessage(text);
        message.setCreatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private Conversation createConversation(Client client) {
        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setChannel(CANAL_WHATSAPP);
        conversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }
}
