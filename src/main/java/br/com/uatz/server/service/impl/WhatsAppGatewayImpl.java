package br.com.uatz.server.service.impl;

import br.com.uatz.server.client.WhatsAppCloudApiClient;
import br.com.uatz.server.dto.whatsapp.WhatsAppSendMessageRequest;
import br.com.uatz.server.dto.whatsapp.WhatsAppSendMessageResponse;
import br.com.uatz.server.dto.whatsapp.WhatsAppSendTextRequest;
import br.com.uatz.server.env.Enviroment;
import br.com.uatz.server.service.WhatsAppGateway;
import br.com.uatz.server.util.StringUtil;
import br.com.uatz.server.util.WhatsAppMessageUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Envio das mensagens ao cliente. Com {@code whatsapp.enabled=true} a mensagem
 * vai pela WhatsApp Cloud API; desligado, apenas para o log — o que mantém o
 * fluxo de fechamento do pedido inteiro exercitável sem número verificado.
 *
 * <p>Uma falha de envio é registrada mas não propagada: o pedido já foi
 * fechado no banco quando a mensagem sai, e derrubar a transação por causa do
 * transporte deixaria o estado do pedido inconsistente com o que o operador
 * viu.</p>
 */
@ApplicationScoped
public class WhatsAppGatewayImpl implements WhatsAppGateway {

    private static final Logger logger = Logger.getLogger(WhatsAppGatewayImpl.class);

    private static final String PREFIXO_BEARER = "Bearer ";
    private static final String PRODUTO_WHATSAPP = "whatsapp";
    private static final String DESTINATARIO_INDIVIDUAL = "individual";
    private static final String TIPO_TEXTO = "text";

    private final Enviroment enviroment;
    private final WhatsAppCloudApiClient whatsAppCloudApiClient;

    public WhatsAppGatewayImpl(
            Enviroment enviroment,
            @RestClient WhatsAppCloudApiClient whatsAppCloudApiClient
    ) {
        this.enviroment = enviroment;
        this.whatsAppCloudApiClient = whatsAppCloudApiClient;
    }

    @Override
    public void sendMessage(String phone, String text) {
        if (!enviroment.isWhatsAppHabilitado()) {
            logger.infof("WhatsApp (simulado) para %s:%n%s", phone, text);
            return;
        }

        String phoneNumberId = enviroment.getWhatsAppPhoneNumberId();
        String tokenAcesso = enviroment.getWhatsAppTokenAcesso();

        if (StringUtil.isNullOrEmpty(phoneNumberId) || StringUtil.isNullOrEmpty(tokenAcesso)) {
            logger.errorf("Envio para %s cancelado: whatsapp.phone-number-id ou whatsapp.access-token não configurados", phone);
            return;
        }

        try {
            WhatsAppSendMessageResponse response = whatsAppCloudApiClient.sendMessage(
                    phoneNumberId,
                    PREFIXO_BEARER + tokenAcesso,
                    buildRequest(phone, text)
            );
            logger.infof("Mensagem enviada para %s: %s", phone, extractMessageId(response));
        } catch (Exception e) {
            logger.errorf(e, "Falha ao enviar a mensagem de WhatsApp para %s", phone);
        }
    }

    private WhatsAppSendMessageRequest buildRequest(String phone, String text) {
        return new WhatsAppSendMessageRequest(
                PRODUTO_WHATSAPP,
                DESTINATARIO_INDIVIDUAL,
                WhatsAppMessageUtil.somenteDigitos(phone),
                TIPO_TEXTO,
                new WhatsAppSendTextRequest(Boolean.FALSE, text)
        );
    }

    private String extractMessageId(WhatsAppSendMessageResponse response) {
        if (response == null || response.messages() == null || response.messages().isEmpty()) {
            return StringUtil.STRING_VAZIA;
        }

        return response.messages().get(0).id();
    }
}
