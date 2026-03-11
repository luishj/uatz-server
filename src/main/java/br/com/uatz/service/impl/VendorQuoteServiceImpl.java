package br.com.uatz.service.impl;

import br.com.uatz.api.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.api.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.api.mapper.VendorQuoteApiMapper;
import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.Vendor;
import br.com.uatz.model.entity.VendorQuote;
import br.com.uatz.model.entity.VendorQuoteItem;
import br.com.uatz.model.enums.BudgetRequestVendorStatus;
import br.com.uatz.repository.BudgetItemRepository;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.VendorQuoteRepository;
import br.com.uatz.repository.VendorRepository;
import br.com.uatz.service.BudgetRequestDistributionService;
import br.com.uatz.service.VendorQuoteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
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
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));

        Vendor vendor = vendorRepository.findOptionalById(request.vendorId())
                .orElseThrow(() -> new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND));

        if (!Boolean.TRUE.equals(vendor.getActive())) {
            throw new WebApplicationException("Vendor is inactive", Response.Status.CONFLICT);
        }

        if (vendorQuoteRepository.findByRequestIdAndVendorId(budgetRequest.getId(), vendor.getId()).isPresent()) {
            throw new WebApplicationException("Vendor can quote this request only once", Response.Status.CONFLICT);
        }

        var assignment = budgetRequestDistributionService.findAssignmentForVendor(budgetRequest.getId(), vendor.getEmail());
        if (assignment.getStatus() == BudgetRequestVendorStatus.DECLINED) {
            throw new WebApplicationException("Vendor has already declined this request", Response.Status.CONFLICT);
        }
        if (assignment.getStatus() == BudgetRequestVendorStatus.RESPONDED) {
            throw new WebApplicationException("Vendor has already responded to this request", Response.Status.CONFLICT);
        }

        List<BudgetItem> budgetItems = budgetItemRepository.findByRequestId(budgetRequest.getId());
        if (budgetItems.isEmpty()) {
            throw new WebApplicationException("Budget request has no items available for quotation", Response.Status.CONFLICT);
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
        return VendorQuoteApiMapper.toSummary(requestId, vendorQuoteRepository.findByRequestId(requestId));
    }

    @Override
    public Optional<VendorQuote> findByRequestIdAndVendorEmail(Long requestId, String email) {
        return vendorRepository.findByEmail(email)
                .flatMap(vendor -> vendorQuoteRepository.findByRequestIdAndVendorId(requestId, vendor.getId()));
    }

    private void validateBudgetRequestExists(Long requestId) {
        if (budgetRequestRepository.findOptionalById(requestId).isEmpty()) {
            throw new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND);
        }
    }

    private void validateVendorExists(Long vendorId) {
        if (vendorRepository.findOptionalById(vendorId).isEmpty()) {
            throw new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND);
        }
    }

    private void validateQuoteItems(VendorQuoteRequest request, Set<Long> availableBudgetItemIds) {
        Set<Long> submittedIds = new HashSet<>();

        for (var itemRequest : request.items()) {
            if (!availableBudgetItemIds.contains(itemRequest.budgetItemId())) {
                throw new WebApplicationException("Quoted item does not belong to this budget request", Response.Status.BAD_REQUEST);
            }
            if (!submittedIds.add(itemRequest.budgetItemId())) {
                throw new WebApplicationException("Quoted items must be unique", Response.Status.BAD_REQUEST);
            }
        }

        if (submittedIds.size() != availableBudgetItemIds.size()) {
            throw new WebApplicationException("All budget request items must be quoted", Response.Status.BAD_REQUEST);
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
            throw new WebApplicationException("Budget quote total must be greater than zero", Response.Status.BAD_REQUEST);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
