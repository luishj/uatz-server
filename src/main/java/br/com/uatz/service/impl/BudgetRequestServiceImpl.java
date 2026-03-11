package br.com.uatz.service.impl;

import br.com.uatz.api.dto.budget.BudgetItemRequest;
import br.com.uatz.api.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.api.mapper.BudgetRequestApiMapper;
import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.Client;
import br.com.uatz.model.entity.Product;
import br.com.uatz.model.enums.BudgetRequestStatus;
import br.com.uatz.repository.BudgetItemRepository;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.ClientRepository;
import br.com.uatz.repository.ProductRepository;
import br.com.uatz.service.BudgetRequestDistributionService;
import br.com.uatz.service.BudgetRequestService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestServiceImpl implements BudgetRequestService {

    private final BudgetRequestRepository budgetRequestRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final BudgetRequestDistributionService budgetRequestDistributionService;

    public BudgetRequestServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            BudgetItemRepository budgetItemRepository,
            ClientRepository clientRepository,
            ProductRepository productRepository,
            BudgetRequestDistributionService budgetRequestDistributionService
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
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

        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setClient(client);
        budgetRequest.setCity(request.city());
        budgetRequest.setStatus(BudgetRequestStatus.OPEN);
        budgetRequest.setCreatedAt(LocalDateTime.now());
        budgetRequestRepository.save(budgetRequest);

        List<BudgetItem> items = request.items() == null
                ? List.of()
                : request.items().stream().map(item -> createItem(budgetRequest, item)).toList();

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
}
