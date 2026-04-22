package br.com.uatz.service.impl;

import br.com.uatz.api.dto.budget.BudgetItemRequest;
import br.com.uatz.api.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.api.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.api.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.api.mapper.BudgetRequestApiMapper;
import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.Client;
import br.com.uatz.model.entity.Conversation;
import br.com.uatz.model.entity.Message;
import br.com.uatz.model.entity.Product;
import br.com.uatz.model.enums.BudgetRequestStatus;
import br.com.uatz.model.enums.MessageDirection;
import br.com.uatz.model.enums.StatusType;
import br.com.uatz.repository.BudgetItemRepository;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.ClientRepository;
import br.com.uatz.repository.ConversationRepository;
import br.com.uatz.repository.MessageRepository;
import br.com.uatz.repository.ProductRepository;
import br.com.uatz.repository.StatusRepository;
import br.com.uatz.service.BudgetRequestDistributionService;
import br.com.uatz.service.BudgetRequestService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
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
    private final BudgetRequestDistributionService budgetRequestDistributionService;

    public BudgetRequestServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            BudgetItemRepository budgetItemRepository,
            ClientRepository clientRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ProductRepository productRepository,
            StatusRepository statusRepository,
            BudgetRequestDistributionService budgetRequestDistributionService
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.clientRepository = clientRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
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
                .orElseThrow(() -> new WebApplicationException("Client not found", Response.Status.NOT_FOUND));

        BudgetRequest budgetRequest = createBudgetRequestBase(client, request.city(), null, null);
        List<BudgetItem> items = persistItems(budgetRequest, request.items());
        return BudgetRequestApiMapper.toResponse(budgetRequest, items);
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

        BudgetRequest budgetRequest = createBudgetRequestBase(client, request.city(), "WHATSAPP", request.message());
        List<BudgetItem> items = persistItems(budgetRequest, parseWhatsAppItems(request.message()));
        return BudgetRequestApiMapper.toResponse(budgetRequest, items);
    }

    @Override
    @Transactional
    public BudgetRequestResponse review(Long id, BudgetRequestReviewRequest request) {
        BudgetRequest budgetRequest = budgetRequestRepository.findOptionalById(id)
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));

        if (request.city() != null && !request.city().isBlank()) {
            budgetRequest.setCity(request.city());
        }
        budgetRequestRepository.save(budgetRequest);

        if (request.items() == null || request.items().isEmpty()) {
            throw new WebApplicationException("Review must contain at least one item", Response.Status.BAD_REQUEST);
        }

        budgetItemRepository.deleteByRequestId(id);
        List<BudgetItem> items = persistItems(budgetRequest, request.items());
        return BudgetRequestApiMapper.toResponse(budgetRequest, items);
    }

    @Override
    public Optional<BudgetRequestResponse> findResponseById(Long id) {
        return budgetRequestRepository.findOptionalById(id)
                .map(budgetRequest -> BudgetRequestApiMapper.toResponse(
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

        return findResponseById(id);
    }

    @Override
    public List<BudgetRequestResponse> findAllWithItems() {
        return budgetRequestRepository.listAllBudgetRequests()
                .stream()
                .map(budgetRequest -> BudgetRequestApiMapper.toResponse(
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

        return budgetRequestRepository.listAllBudgetRequests()
                .stream()
                .filter(budgetRequest -> allowedRequestIds.contains(budgetRequest.getId()))
                .map(budgetRequest -> BudgetRequestApiMapper.toResponse(
                        budgetRequest,
                        budgetItemRepository.findByRequestId(budgetRequest.getId())
                ))
                .toList();
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
                .orElseThrow(() -> new WebApplicationException("Product not found", Response.Status.NOT_FOUND));
    }

    private BudgetRequest createBudgetRequestBase(Client client, String city, String sourceChannel, String sourceMessage) {
        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setClient(client);
        budgetRequest.setCity(city);
        budgetRequest.setSourceChannel(sourceChannel);
        budgetRequest.setSourceMessage(sourceMessage);
        budgetRequest.setStatusEntity(statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST, BudgetRequestStatus.OPEN.name())
                .orElseThrow(() -> new WebApplicationException("Status not found", Response.Status.INTERNAL_SERVER_ERROR)));
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
