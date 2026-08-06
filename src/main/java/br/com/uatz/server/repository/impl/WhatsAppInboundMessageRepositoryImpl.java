package br.com.uatz.server.repository.impl;

import br.com.uatz.model.WhatsAppInboundMessage;
import br.com.uatz.server.repository.WhatsAppInboundMessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class WhatsAppInboundMessageRepositoryImpl extends GenericRepositoryImpl<WhatsAppInboundMessage, Long> implements WhatsAppInboundMessageRepository {

    /**
     * {@code REQUIRES_NEW} isola a trava do processamento da mensagem: o insert
     * comita sozinho, antes de qualquer trabalho, então mesmo que o
     * processamento falhe depois a mensagem continua marcada como vista
     * (at-most-once — o certo aqui, porque um pedido duplicado na frente do
     * lojista é pior do que uma mensagem perdida, que o cliente reenvia).
     *
     * <p>O {@code persistAndFlush} força o INSERT dentro deste método para que a
     * violação da unique seja capturada aqui e vire {@code false}, em vez de
     * estourar no commit lá fora.</p>
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean claim(String messageId) {
        WhatsAppInboundMessage registro = new WhatsAppInboundMessage();
        registro.setMessageId(messageId);
        registro.setReceivedAt(LocalDateTime.now());

        try {
            persistAndFlush(registro);
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }
}
