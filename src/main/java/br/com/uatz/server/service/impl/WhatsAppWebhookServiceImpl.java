package br.com.uatz.server.service.impl;

import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.Conversation;
import br.com.uatz.server.dto.whatsapp.WhatsAppWebhookChange;
import br.com.uatz.server.dto.whatsapp.WhatsAppWebhookEntry;
import br.com.uatz.server.dto.whatsapp.WhatsAppWebhookMessage;
import br.com.uatz.server.dto.whatsapp.WhatsAppWebhookPayload;
import br.com.uatz.server.env.Enviroment;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.repository.BudgetRequestRepository;
import br.com.uatz.server.repository.VendorQuoteRepository;
import br.com.uatz.server.service.BudgetRequestClosingService;
import br.com.uatz.server.service.BudgetRequestService;
import br.com.uatz.server.service.ConversationService;
import br.com.uatz.server.service.WhatsAppGateway;
import br.com.uatz.server.service.WhatsAppWebhookService;
import br.com.uatz.server.util.StringUtil;
import br.com.uatz.server.util.WhatsAppMessageUtil;
import br.com.uatz.server.util.WhatsAppSignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

/**
 * Traduz as notificações da Cloud API em ações de negócio.
 *
 * <p>Uma mensagem de texto pode significar duas coisas: quando o cliente já
 * recebeu as opções de um pedido em aberto e responde apenas um número, é a
 * escolha da cotação; em qualquer outro caso é um novo pedido de orçamento.</p>
 */
@ApplicationScoped
public class WhatsAppWebhookServiceImpl implements WhatsAppWebhookService {

    private static final Logger logger = Logger.getLogger(WhatsAppWebhookServiceImpl.class);

    private static final String MODO_INSCRICAO = "subscribe";
    private static final String TIPO_TEXTO = "text";
    private static final String PADRAO_OPCAO = "\\d{1,3}";

    private final Enviroment enviroment;
    private final ObjectMapper objectMapper;
    private final BudgetRequestRepository budgetRequestRepository;
    private final VendorQuoteRepository vendorQuoteRepository;
    private final BudgetRequestService budgetRequestService;
    private final BudgetRequestClosingService budgetRequestClosingService;
    private final ConversationService conversationService;
    private final WhatsAppGateway whatsAppGateway;

    public WhatsAppWebhookServiceImpl(
            Enviroment enviroment,
            ObjectMapper objectMapper,
            BudgetRequestRepository budgetRequestRepository,
            VendorQuoteRepository vendorQuoteRepository,
            BudgetRequestService budgetRequestService,
            BudgetRequestClosingService budgetRequestClosingService,
            ConversationService conversationService,
            WhatsAppGateway whatsAppGateway
    ) {
        this.enviroment = enviroment;
        this.objectMapper = objectMapper;
        this.budgetRequestRepository = budgetRequestRepository;
        this.vendorQuoteRepository = vendorQuoteRepository;
        this.budgetRequestService = budgetRequestService;
        this.budgetRequestClosingService = budgetRequestClosingService;
        this.conversationService = conversationService;
        this.whatsAppGateway = whatsAppGateway;
    }

    @Override
    public String verify(String mode, String token, String challenge) {
        String tokenEsperado = enviroment.getWhatsAppTokenVerificacao();

        if (StringUtil.isNullOrEmpty(tokenEsperado)) {
            logger.warn("Verificação do webhook recusada: whatsapp.verify-token não está configurado");
            throw MessageBuilder.build(CloudMessage.WEBHOOK_TOKEN_INVALIDO, Status.FORBIDDEN);
        }

        if (!MODO_INSCRICAO.equals(mode) || !tokenEsperado.equals(token)) {
            throw MessageBuilder.build(CloudMessage.WEBHOOK_TOKEN_INVALIDO, Status.FORBIDDEN);
        }

        return StringUtil.naoNulo(challenge);
    }

    @Override
    public void process(String signature, String payload) {
        validateSignature(signature, payload);

        for (WhatsAppWebhookMessage message : extractMessages(deserialize(payload))) {
            handleMessage(message);
        }
    }

    /**
     * Sem app secret configurado a conferência é pulada, o que só é aceitável
     * em desenvolvimento — em produção qualquer um poderia postar no webhook.
     */
    private void validateSignature(String signature, String payload) {
        String segredoApp = enviroment.getWhatsAppSegredoApp();

        if (StringUtil.isNullOrEmpty(segredoApp)) {
            logger.warn("Webhook aceito sem conferir a assinatura: whatsapp.app-secret não está configurado");
            return;
        }

        if (!WhatsAppSignatureUtil.assinaturaValida(segredoApp, payload, signature)) {
            throw MessageBuilder.build(CloudMessage.WEBHOOK_ASSINATURA_INVALIDA, Status.FORBIDDEN);
        }
    }

    private WhatsAppWebhookPayload deserialize(String payload) {
        try {
            return objectMapper.readValue(StringUtil.naoNulo(payload), WhatsAppWebhookPayload.class);
        } catch (Exception e) {
            logger.errorf(e, "Payload do webhook do WhatsApp não pôde ser lido");
            throw MessageBuilder.build(CloudMessage.WEBHOOK_PAYLOAD_INVALIDO, Status.BAD_REQUEST);
        }
    }

    /**
     * Achata a estrutura entry -> changes -> value -> messages, que a Cloud API
     * envia em lote e com todos os níveis opcionais.
     */
    private List<WhatsAppWebhookMessage> extractMessages(WhatsAppWebhookPayload payload) {
        if (payload == null || payload.entry() == null) {
            return List.of();
        }

        return payload.entry().stream()
                .filter(entry -> entry != null && entry.changes() != null)
                .map(WhatsAppWebhookEntry::changes)
                .flatMap(List::stream)
                .map(WhatsAppWebhookChange::value)
                .filter(value -> value != null && value.messages() != null)
                .flatMap(value -> value.messages().stream())
                .filter(message -> message != null)
                .toList();
    }

    /**
     * Uma falha em uma mensagem não pode derrubar o lote: a Meta reenvia o
     * evento inteiro quando o webhook não responde 200, o que reprocessaria as
     * mensagens que já deram certo.
     */
    private void handleMessage(WhatsAppWebhookMessage message) {
        if (!TIPO_TEXTO.equals(message.type()) || message.text() == null) {
            logger.infof("Mensagem %s ignorada: tipo %s não é tratado", message.id(), message.type());
            return;
        }

        String phone = WhatsAppMessageUtil.somenteDigitos(message.from());
        String text = message.text().body();

        if (StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(text)) {
            logger.warnf("Mensagem %s ignorada: telefone ou texto vazio", message.id());
            return;
        }

        try {
            handleText(phone, text.trim());
        } catch (Exception e) {
            logger.errorf(e, "Falha ao processar a mensagem %s de %s", message.id(), phone);
        }
    }

    private void handleText(String phone, String text) {
        Integer optionNumber = parseOptionNumber(text);

        Optional<BudgetRequest> awaitingSelection = optionNumber == null
                ? Optional.empty()
                : budgetRequestRepository.findLastAwaitingSelectionByClientPhone(phone);

        if (awaitingSelection.isEmpty()) {
            budgetRequestService.createFromWhatsAppMessage(phone, null, null, text);
            return;
        }

        selectOption(awaitingSelection.get(), optionNumber, text);
    }

    private void selectOption(BudgetRequest budgetRequest, Integer optionNumber, String text) {
        Conversation conversation = conversationService.resolveConversation(budgetRequest.getClient());
        conversationService.registerInbound(conversation, text);

        boolean opcaoExiste = vendorQuoteRepository
                .findByRequestIdAndOptionNumber(budgetRequest.getId(), optionNumber)
                .isPresent();

        if (!opcaoExiste) {
            String aviso = WhatsAppMessageUtil.montarMensagemOpcaoInvalida(budgetRequest.getId(), optionNumber);
            whatsAppGateway.sendMessage(budgetRequest.getClient().getPhone(), aviso);
            conversationService.registerOutbound(conversation, aviso);
            return;
        }

        budgetRequestClosingService.selectOption(budgetRequest.getId(), optionNumber);
    }

    /**
     * Só um número isolado conta como escolha. "10 sacos de cimento" continua
     * sendo um pedido novo.
     */
    private Integer parseOptionNumber(String text) {
        if (!text.matches(PADRAO_OPCAO)) {
            return null;
        }

        return Integer.valueOf(text);
    }
}
