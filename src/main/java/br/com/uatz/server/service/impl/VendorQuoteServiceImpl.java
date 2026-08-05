package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.server.mapping.VendorQuoteMapping;
import br.com.uatz.model.BudgetItem;
import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.Vendor;
import br.com.uatz.model.VendorQuote;
import br.com.uatz.model.VendorQuoteItem;
import br.com.uatz.model.enumerador.BudgetRequestStatus;
import br.com.uatz.model.enumerador.BudgetRequestVendorStatus;
import br.com.uatz.server.repository.BudgetItemRepository;
import br.com.uatz.server.repository.BudgetRequestRepository;
import br.com.uatz.server.repository.VendorQuoteRepository;
import br.com.uatz.server.repository.VendorRepository;
import br.com.uatz.server.service.BudgetRequestDistributionService;
import br.com.uatz.server.service.VendorQuoteService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class VendorQuoteServiceImpl implements VendorQuoteService {

    private final VendorQuoteRepository vendorQuoteRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetRequestRepository budgetRequestRepository;
    private final VendorRepository vendorRepository;
    private final BudgetRequestDistributionService budgetRequestDistributionService;

    public VendorQuoteServiceImpl(
            VendorQuoteRepository vendorQuoteRepository,
            BudgetItemRepository budgetItemRepository,
            BudgetRequestRepository budgetRequestRepository,
            VendorRepository vendorRepository,
            BudgetRequestDistributionService budgetRequestDistributionService
    ) {
        this.vendorQuoteRepository = vendorQuoteRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetRequestRepository = budgetRequestRepository;
        this.vendorRepository = vendorRepository;
        this.budgetRequestDistributionService = budgetRequestDistributionService;
    }

    @Override
    @Transactional
    public VendorQuote create(VendorQuoteRequest request) {
        BudgetRequest budgetRequest = budgetRequestRepository.findOptionalById(request.requestId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND));

        if (budgetRequest.getStatus() == BudgetRequestStatus.CLOSED) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_JA_FECHADO, Status.CONFLICT);
        }

        Vendor vendor = vendorRepository.findOptionalById(request.vendorId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        if (!Boolean.TRUE.equals(vendor.getActive())) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_INATIVO, Status.CONFLICT);
        }

        if (vendorQuoteRepository.findByRequestIdAndVendorId(budgetRequest.getId(), vendor.getId()).isPresent()) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_JA_COTOU_PEDIDO, Status.CONFLICT);
        }

        var assignment = budgetRequestDistributionService.findAssignmentForVendor(budgetRequest.getId(), vendor.getEmail());
        if (assignment.getStatus() == BudgetRequestVendorStatus.DECLINED) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_JA_RECUSOU_PEDIDO, Status.CONFLICT);
        }
        if (assignment.getStatus() == BudgetRequestVendorStatus.RESPONDED) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_JA_RESPONDEU_PEDIDO, Status.CONFLICT);
        }

        List<BudgetItem> budgetItems = budgetItemRepository.findByRequestId(budgetRequest.getId());
        if (budgetItems.isEmpty()) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_SEM_ITENS_PARA_COTACAO, Status.CONFLICT);
        }

        Map<Long, BudgetItem> budgetItemsById = budgetItems.stream()
                .collect(Collectors.toMap(BudgetItem::getId, Function.identity()));

        validateQuoteItems(request, budgetItemsById.keySet());

        VendorQuote vendorQuote = new VendorQuote();
        vendorQuote.setRequest(budgetRequest);
        vendorQuote.setVendor(vendor);
        vendorQuote.setMessage(request.message());
        vendorQuote.setCreatedAt(LocalDateTime.now());
        vendorQuote.setTotalPrice(buildQuoteItems(request, budgetItemsById, vendorQuote));
        VendorQuote savedQuote = vendorQuoteRepository.save(vendorQuote);
        budgetRequestDistributionService.markResponded(budgetRequest.getId(), vendor.getId());
        return savedQuote;
    }

    @Override
    public Optional<VendorQuote> findById(Long id) {
        return vendorQuoteRepository.findOptionalById(id);
    }

    @Override
    public List<VendorQuote> findByRequestId(Long requestId) {
        validateBudgetRequestExists(requestId);
        return vendorQuoteRepository.findByRequestId(requestId);
    }

    @Override
    public List<VendorQuote> findByVendorId(Long vendorId) {
        validateVendorExists(vendorId);
        return vendorQuoteRepository.findByVendorId(vendorId);
    }

    @Override
    public VendorQuoteSummaryResponse summarizeByRequestId(Long requestId) {
        validateBudgetRequestExists(requestId);
        return VendorQuoteMapping.toSummary(requestId, vendorQuoteRepository.findByRequestId(requestId));
    }

    @Override
    public Optional<VendorQuote> findByRequestIdAndVendorEmail(Long requestId, String email) {
        return vendorRepository.findByEmail(email)
                .flatMap(vendor -> vendorQuoteRepository.findByRequestIdAndVendorId(requestId, vendor.getId()));
    }

    private void validateBudgetRequestExists(Long requestId) {
        if (budgetRequestRepository.findOptionalById(requestId).isEmpty()) {
            throw MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND);
        }
    }

    private void validateVendorExists(Long vendorId) {
        if (vendorRepository.findOptionalById(vendorId).isEmpty()) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND);
        }
    }

    private void validateQuoteItems(VendorQuoteRequest request, Set<Long> availableBudgetItemIds) {
        Set<Long> submittedIds = new HashSet<>();

        for (var itemRequest : request.items()) {
            if (!availableBudgetItemIds.contains(itemRequest.budgetItemId())) {
                throw MessageBuilder.build(CloudMessage.ITEM_COTADO_NAO_PERTENCE_PEDIDO, Status.BAD_REQUEST);
            }
            if (!submittedIds.add(itemRequest.budgetItemId())) {
                throw MessageBuilder.build(CloudMessage.ITEM_COTADO_DUPLICADO, Status.BAD_REQUEST);
            }
        }

        if (submittedIds.size() != availableBudgetItemIds.size()) {
            throw MessageBuilder.build(CloudMessage.TODOS_ITENS_DEVEM_SER_COTADOS, Status.BAD_REQUEST);
        }
    }

    private BigDecimal buildQuoteItems(VendorQuoteRequest request, Map<Long, BudgetItem> budgetItemsById, VendorQuote vendorQuote) {
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            BudgetItem budgetItem = budgetItemsById.get(itemRequest.budgetItemId());
            BigDecimal unitPrice = itemRequest.unitPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = budgetItem.getQuantity().multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);

            VendorQuoteItem quoteItem = new VendorQuoteItem();
            quoteItem.setBudgetItemId(budgetItem.getId());
            quoteItem.setProductName(budgetItem.getProductName());
            quoteItem.setQuantity(budgetItem.getQuantity());
            quoteItem.setUnit(budgetItem.getUnit());
            quoteItem.setUnitPrice(unitPrice);
            quoteItem.setLineTotal(lineTotal);
            vendorQuote.addItem(quoteItem);

            total = total.add(lineTotal);
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw MessageBuilder.build(CloudMessage.TOTAL_COTACAO_INVALIDO, Status.BAD_REQUEST);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
