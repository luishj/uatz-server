package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.budget.BudgetItemRequest;
import br.com.uatz.server.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.server.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.server.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.server.mapping.BudgetRequestMapping;
import br.com.uatz.model.BudgetItem;
import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.Client;
import br.com.uatz.model.Conversation;
import br.com.uatz.model.Message;
import br.com.uatz.model.Product;
import br.com.uatz.model.Vendor;
import br.com.uatz.model.enumerador.BudgetRequestStatus;
import br.com.uatz.model.enumerador.MessageDirection;
import br.com.uatz.model.enumerador.StatusType;
import br.com.uatz.server.repository.BudgetItemRepository;
import br.com.uatz.server.repository.BudgetRequestRepository;
import br.com.uatz.server.repository.ClientRepository;
import br.com.uatz.server.repository.ConversationRepository;
import br.com.uatz.server.repository.MessageRepository;
import br.com.uatz.server.repository.ProductRepository;
import br.com.uatz.server.repository.StatusRepository;
import br.com.uatz.server.repository.VendorRepository;
import br.com.uatz.server.service.BudgetRequestDistributionService;
import br.com.uatz.server.service.BudgetRequestService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestServiceImpl implements BudgetRequestService {

    private final BudgetRequestRepository budgetRequestRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ClientRepository clientRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ProductRepository productRepository;
    private final StatusRepository statusRepository;
    private final VendorRepository vendorRepository;
    private final BudgetRequestDistributionService budgetRequestDistributionService;

    public BudgetRequestServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            BudgetItemRepository budgetItemRepository,
            ClientRepository clientRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ProductRepository productRepository,
            StatusRepository statusRepository,
            VendorRepository vendorRepository,
            BudgetRequestDistributionService budgetRequestDistributionService
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.clientRepository = clientRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
        this.vendorRepository = vendorRepository;
        this.budgetRequestDistributionService = budgetRequestDistributionService;
    }

    @Override
    public BudgetRequest save(BudgetRequest budgetRequest) {
        return budgetRequestRepository.save(budgetRequest);
    }

    @Override
    public Optional<BudgetRequest> findById(Long id) {
        return budgetRequestRepository.findOptionalById(id);
    }

    @Override
    public List<BudgetRequest> findAll() {
        return budgetRequestRepository.listAllBudgetRequests();
    }

    @Override
    @Transactional
    public BudgetRequestResponse create(BudgetRequestCreateRequest request) {
        Client client = clientRepository.findOptionalById(request.clientId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.CLIENTE_NAO_ENCONTRADO, Status.NOT_FOUND));

        BudgetRequest budgetRequest = createBudgetRequestBase(client, request.city(), null, null, null);
        List<BudgetItem> items = persistItems(budgetRequest, request.items());
        return BudgetRequestMapping.toResponse(budgetRequest, items);
    }

    @Override
    @Transactional
    public BudgetRequestResponse createFromWhatsAppSimulation(WhatsAppSimulationRequest request) {
        Client client = clientRepository.findByPhone(request.phone())
                .map(existing -> updateClientLocation(existing, request.city(), request.state()))
                .orElseGet(() -> createClientFromSimulation(request));

        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setChannel("WHATSAPP");
        conversation.setCreatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        Message message = new Message();
        message.setConversation(conversation);
        message.setDirection(MessageDirection.IN);
        message.setMessage(request.message());
        message.setCreatedAt(LocalDateTime.now());
        messageRepository.save(message);

        BudgetRequest budgetRequest = createBudgetRequestBase(client, request.city(), "WHATSAPP", request.message(), conversation);
        List<BudgetItem> items = persistItems(budgetRequest, parseWhatsAppItems(request.message()));
        return BudgetRequestMapping.toResponse(budgetRequest, items);
    }

    @Override
    @Transactional
    public BudgetRequestResponse review(Long id, BudgetRequestReviewRequest request) {
        BudgetRequest budgetRequest = budgetRequestRepository.findOptionalById(id)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND));

        if (request.city() != null && !request.city().isBlank()) {
            budgetRequest.setCity(request.city());
        }
        budgetRequestRepository.save(budgetRequest);

        if (request.items() == null || request.items().isEmpty()) {
            throw MessageBuilder.build(CloudMessage.REVISAO_SEM_ITENS, Status.BAD_REQUEST);
        }

        budgetItemRepository.deleteByRequestId(id);
        List<BudgetItem> items = persistItems(budgetRequest, request.items());
        return BudgetRequestMapping.toResponse(budgetRequest, items);
    }

    @Override
    public Optional<BudgetRequestResponse> findResponseById(Long id) {
        return budgetRequestRepository.findOptionalById(id)
                .map(budgetRequest -> BudgetRequestMapping.toResponse(
                        budgetRequest,
                        budgetItemRepository.findByRequestId(budgetRequest.getId())
                ));
    }

    @Override
    public Optional<BudgetRequestResponse> findResponseByIdForVendor(Long id, String vendorEmail) {
        List<Long> allowedRequestIds = budgetRequestDistributionService.findAssignedRequestIdsForVendor(vendorEmail);

        if (!allowedRequestIds.contains(id)) {
            return Optional.empty();
        }

        Long vendorId = resolveVendorId(vendorEmail);

        return budgetRequestRepository.findOptionalById(id)
                .map(budgetRequest -> BudgetRequestMapping.toVendorResponse(
                        budgetRequest,
                        budgetItemRepository.findByRequestId(budgetRequest.getId()),
                        vendorId
                ));
    }

    @Override
    public List<BudgetRequestResponse> findAllWithItems() {
        return budgetRequestRepository.listAllBudgetRequests()
                .stream()
                .map(budgetRequest -> BudgetRequestMapping.toResponse(
                        budgetRequest,
                        budgetItemRepository.findByRequestId(budgetRequest.getId())
                ))
                .toList();
    }

    @Override
    public List<BudgetRequestResponse> findAllWithItemsForVendor(String vendorEmail) {
        List<Long> allowedRequestIds = budgetRequestDistributionService.findAssignedRequestIdsForVendor(vendorEmail);

        if (allowedRequestIds.isEmpty()) {
            return List.of();
        }

        Long vendorId = resolveVendorId(vendorEmail);

        return budgetRequestRepository.listAllBudgetRequests()
                .stream()
                .filter(budgetRequest -> allowedRequestIds.contains(budgetRequest.getId()))
                .map(budgetRequest -> BudgetRequestMapping.toVendorResponse(
                        budgetRequest,
                        budgetItemRepository.findByRequestId(budgetRequest.getId()),
                        vendorId
                ))
                .toList();
    }

    private Long resolveVendorId(String vendorEmail) {
        return vendorRepository.findByEmail(vendorEmail)
                .map(Vendor::getId)
                .orElse(null);
    }

    private BudgetItem createItem(BudgetRequest budgetRequest, BudgetItemRequest request) {
        BudgetItem item = new BudgetItem();
        item.setRequest(budgetRequest);
        item.setProduct(resolveProduct(request.productId()));
        item.setProductName(request.productName());
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());
        return budgetItemRepository.save(item);
    }

    private Product resolveProduct(Long productId) {
        if (productId == null) {
            return null;
        }

        return productRepository.findOptionalById(productId)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PRODUTO_NAO_ENCONTRADO, Status.NOT_FOUND));
    }

    private BudgetRequest createBudgetRequestBase(Client client, String city, String sourceChannel, String sourceMessage, Conversation conversation) {
        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setClient(client);
        budgetRequest.setCity(city);
        budgetRequest.setSourceChannel(sourceChannel);
        budgetRequest.setSourceMessage(sourceMessage);
        budgetRequest.setConversation(conversation);
        budgetRequest.setStatusEntity(statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST, BudgetRequestStatus.OPEN.name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.SITUACAO_NAO_ENCONTRADA, Status.INTERNAL_SERVER_ERROR)));
        budgetRequest.setCreatedAt(LocalDateTime.now());
        budgetRequestRepository.save(budgetRequest);
        return budgetRequest;
    }

    private List<BudgetItem> persistItems(BudgetRequest budgetRequest, List<BudgetItemRequest> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        return items.stream()
                .map(item -> createItem(budgetRequest, item))
                .toList();
    }

    private Client createClientFromSimulation(WhatsAppSimulationRequest request) {
        Client client = new Client();
        client.setPhone(request.phone());
        client.setCity(request.city());
        client.setState(request.state());
        client.setCreatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    private Client updateClientLocation(Client client, String city, String state) {
        if (city != null && !city.isBlank()) {
            client.setCity(city);
        }
        if (state != null && !state.isBlank()) {
            client.setState(state);
        }
        return clientRepository.save(client);
    }

    private List<BudgetItemRequest> parseWhatsAppItems(String rawMessage) {
        List<String> rawItems = splitRawItems(rawMessage);
        List<BudgetItemRequest> items = new ArrayList<>();

        for (String rawItem : rawItems) {
            String cleaned = rawItem.trim();
            if (cleaned.isBlank()) {
                continue;
            }

            BigDecimal quantity = BigDecimal.ONE;
            String description = cleaned;
            String unit = "un";

            String[] tokens = cleaned.split("\\s+", 2);
            if (tokens.length > 1 && tokens[0].matches("\\d+[\\.,]?\\d*")) {
                quantity = new BigDecimal(tokens[0].replace(",", "."));
                description = tokens[1].trim();
            }

            if (description.toLowerCase().contains("saco")) {
                unit = "saco";
            } else if (description.toLowerCase().contains("metro")) {
                unit = "m";
            } else if (description.toLowerCase().contains("caixa")) {
                unit = "caixa";
            }

            items.add(new BudgetItemRequest(null, description, quantity, unit));
        }

        if (items.isEmpty()) {
            items.add(new BudgetItemRequest(null, rawMessage.trim(), BigDecimal.ONE, "un"));
        }

        return items;
    }

    private List<String> splitRawItems(String rawMessage) {
        if (rawMessage.contains("\n")) {
            return rawMessage.lines().toList();
        }
        if (rawMessage.contains(";")) {
            return List.of(rawMessage.split(";"));
        }
        if (rawMessage.contains(",")) {
            return List.of(rawMessage.split(","));
        }
        return List.of(rawMessage);
    }
}
