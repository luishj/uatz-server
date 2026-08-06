package br.com.uatz.server.service.impl;

import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.Conversation;
import br.com.uatz.model.StatusEntity;
import br.com.uatz.model.Vendor;
import br.com.uatz.model.VendorQuote;
import br.com.uatz.model.enumerador.BudgetRequestStatus;
import br.com.uatz.model.enumerador.StatusType;
import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionResponse;
import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionsResponse;
import br.com.uatz.server.dto.budget.BudgetRequestSelectionResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.BudgetRequestClosingMapping;
import br.com.uatz.server.repository.BudgetRequestRepository;
import br.com.uatz.server.repository.StatusRepository;
import br.com.uatz.server.repository.VendorQuoteRepository;
import br.com.uatz.server.repository.VendorRepository;
import br.com.uatz.server.service.BudgetRequestClosingService;
import br.com.uatz.server.service.ConversationService;
import br.com.uatz.server.service.WhatsAppGateway;
import br.com.uatz.server.util.WhatsAppMessageUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class BudgetRequestClosingServiceImpl implements BudgetRequestClosingService {

    private final BudgetRequestRepository budgetRequestRepository;
    private final VendorQuoteRepository vendorQuoteRepository;
    private final StatusRepository statusRepository;
    private final VendorRepository vendorRepository;
    private final WhatsAppGateway whatsAppGateway;
    private final ConversationService conversationService;

    public BudgetRequestClosingServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            VendorQuoteRepository vendorQuoteRepository,
            StatusRepository statusRepository,
            VendorRepository vendorRepository,
            WhatsAppGateway whatsAppGateway,
            ConversationService conversationService
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.vendorQuoteRepository = vendorQuoteRepository;
        this.statusRepository = statusRepository;
        this.vendorRepository = vendorRepository;
        this.whatsAppGateway = whatsAppGateway;
        this.conversationService = conversationService;
    }

    @Override
    @Transactional
    public BudgetRequestQuoteOptionsResponse sendQuoteOptions(Long requestId) {
        BudgetRequest budgetRequest = findBudgetRequest(requestId);
        validateNotClosed(budgetRequest);

        List<VendorQuote> quotes = vendorQuoteRepository.findByRequestId(requestId);

        if (quotes.isEmpty()) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_SEM_COTACOES_PARA_ENVIAR, Status.CONFLICT);
        }

        List<BudgetRequestQuoteOptionResponse> options = numberOptions(quotes);
        String clientMessage = WhatsAppMessageUtil.montarMensagemOpcoes(requestId, options);

        budgetRequest.setQuotesSentAt(LocalDateTime.now());
        budgetRequestRepository.save(budgetRequest);
        sendToClient(budgetRequest, clientMessage);

        return new BudgetRequestQuoteOptionsResponse(
                requestId,
                budgetRequest.getQuotesSentAt(),
                clientMessage,
                options
        );
    }

    @Override
    @Transactional
    public BudgetRequestSelectionResponse selectOption(Long requestId, Integer optionNumber) {
        BudgetRequest budgetRequest = findBudgetRequest(requestId);
        validateNotClosed(budgetRequest);

        if (budgetRequest.getQuotesSentAt() == null) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_SEM_OPCOES_ENVIADAS, Status.CONFLICT);
        }

        VendorQuote selectedQuote = vendorQuoteRepository.findByRequestIdAndOptionNumber(requestId, optionNumber)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.OPCAO_COTACAO_NAO_ENCONTRADA, Status.NOT_FOUND));

        budgetRequest.setSelectedQuote(selectedQuote);
        budgetRequest.setClosedAt(LocalDateTime.now());
        budgetRequest.setStatusEntity(resolveClosedStatus());
        budgetRequestRepository.save(budgetRequest);

        BudgetRequestSelectionResponse response = buildSelectionResponse(budgetRequest, selectedQuote);
        sendToClient(budgetRequest, response.clientMessage());
        return response;
    }

    @Override
    public BudgetRequestSelectionResponse findSelection(Long requestId) {
        BudgetRequest budgetRequest = findBudgetRequest(requestId);
        return buildSelectionResponse(budgetRequest, requireSelectedQuote(budgetRequest));
    }

    @Override
    public BudgetRequestSelectionResponse findSelectionForVendor(Long requestId, String vendorEmail) {
        BudgetRequest budgetRequest = findBudgetRequest(requestId);
        VendorQuote selectedQuote = requireSelectedQuote(budgetRequest);

        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        if (!vendor.getId().equals(selectedQuote.getVendor().getId())) {
            return new BudgetRequestSelectionResponse(
                    budgetRequest.getId(),
                    budgetRequest.getStatus(),
                    budgetRequest.getClosedAt(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return buildSelectionResponse(budgetRequest, selectedQuote);
    }

    /**
     * Numera as cotações na ordem apresentada ao cliente: menor total primeiro.
     */
    private List<BudgetRequestQuoteOptionResponse> numberOptions(List<VendorQuote> quotes) {
        List<VendorQuote> orderedQuotes = quotes.stream()
                .sorted(Comparator.comparing(VendorQuote::getTotalPrice))
                .toList();

        List<BudgetRequestQuoteOptionResponse> options = new ArrayList<>();
        int optionNumber = 1;

        for (VendorQuote quote : orderedQuotes) {
            quote.setOptionNumber(optionNumber);
            vendorQuoteRepository.save(quote);
            options.add(BudgetRequestClosingMapping.toOptionResponse(quote));
            optionNumber++;
        }

        return options;
    }

    private BudgetRequestSelectionResponse buildSelectionResponse(BudgetRequest budgetRequest, VendorQuote selectedQuote) {
        Vendor vendor = selectedQuote.getVendor();
        String clientPhone = budgetRequest.getClient().getPhone();

        List<String> products = selectedQuote.getItems()
                .stream()
                .map(item -> WhatsAppMessageUtil.montarDescricaoItem(item.getQuantity(), item.getUnit(), item.getProductName()))
                .toList();

        String clientLinkToVendor = WhatsAppMessageUtil.montarLink(
                vendor.getPhone(),
                WhatsAppMessageUtil.montarTextoClienteParaVendedor(budgetRequest.getId(), products, selectedQuote.getTotalPrice())
        );

        String vendorLinkToClient = WhatsAppMessageUtil.montarLink(
                clientPhone,
                WhatsAppMessageUtil.montarTextoFornecedorParaCliente(budgetRequest.getId(), vendor.getName())
        );

        String clientMessage = WhatsAppMessageUtil.montarMensagemEscolha(
                budgetRequest.getId(),
                vendor.getName(),
                selectedQuote.getTotalPrice(),
                clientLinkToVendor
        );

        return new BudgetRequestSelectionResponse(
                budgetRequest.getId(),
                budgetRequest.getStatus(),
                budgetRequest.getClosedAt(),
                BudgetRequestClosingMapping.toOptionResponse(selectedQuote),
                clientPhone,
                vendor.getPhone(),
                clientMessage,
                clientLinkToVendor,
                vendorLinkToClient
        );
    }

    /**
     * Entrega o texto ao gateway e registra a mensagem na conversa do cliente.
     */
    private void sendToClient(BudgetRequest budgetRequest, String text) {
        whatsAppGateway.sendMessage(budgetRequest.getClient().getPhone(), text);
        conversationService.registerOutbound(resolveConversation(budgetRequest), text);
    }

    /**
     * Conversa em que as mensagens de saída são gravadas. Pedidos criados sem
     * WhatsApp (pelo painel) não têm conversa, então ela é vinculada na primeira
     * mensagem enviada.
     */
    private Conversation resolveConversation(BudgetRequest budgetRequest) {
        if (budgetRequest.getConversation() != null) {
            return budgetRequest.getConversation();
        }

        Conversation conversation = conversationService.resolveConversation(budgetRequest.getClient());
        budgetRequest.setConversation(conversation);
        budgetRequestRepository.save(budgetRequest);
        return conversation;
    }

    private BudgetRequest findBudgetRequest(Long requestId) {
        return budgetRequestRepository.findOptionalById(requestId)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND));
    }

    private VendorQuote requireSelectedQuote(BudgetRequest budgetRequest) {
        VendorQuote selectedQuote = budgetRequest.getSelectedQuote();

        if (selectedQuote == null) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_SEM_ESCOLHA, Status.NOT_FOUND);
        }

        return selectedQuote;
    }

    private void validateNotClosed(BudgetRequest budgetRequest) {
        if (budgetRequest.getStatus() == BudgetRequestStatus.CLOSED) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_JA_FECHADO, Status.CONFLICT);
        }
    }

    private StatusEntity resolveClosedStatus() {
        return statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST, BudgetRequestStatus.CLOSED.name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.SITUACAO_NAO_ENCONTRADA, Status.INTERNAL_SERVER_ERROR));
    }
}
