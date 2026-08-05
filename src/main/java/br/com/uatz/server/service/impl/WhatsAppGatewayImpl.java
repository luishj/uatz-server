package br.com.uatz.server.service.impl;

import br.com.uatz.server.service.WhatsAppGateway;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Implementação provisória do gateway: registra a mensagem no log em vez de
 * chamar a WhatsApp Cloud API. Mantém o fluxo de fechamento do pedido inteiro
 * exercitável sem número verificado nem webhook publicado.
 *
 * <p>Ao ligar a integração real, esta classe passa a ser a alternativa de
 * desenvolvimento e a implementação da Cloud API assume o bean padrão.</p>
 */
@ApplicationScoped
public class WhatsAppGatewayImpl implements WhatsAppGateway {

    private static final Logger logger = Logger.getLogger(WhatsAppGatewayImpl.class);

    @Override
    public void sendMessage(String phone, String text) {
        logger.infof("WhatsApp (simulado) para %s:%n%s", phone, text);
    }

}
