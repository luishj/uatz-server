package br.com.uatz.service.impl;

import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.BudgetRequestVendor;
import br.com.uatz.model.entity.Vendor;
import br.com.uatz.model.enums.BudgetRequestStatus;
import br.com.uatz.model.enums.BudgetRequestVendorStatus;
import br.com.uatz.repository.BudgetItemRepository;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.BudgetRequestVendorRepository;
import br.com.uatz.repository.VendorProductRepository;
import br.com.uatz.repository.VendorRepository;
import br.com.uatz.service.BudgetRequestDistributionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class BudgetRequestDistributionServiceImpl implements BudgetRequestDistributionService {

    private final BudgetRequestRepository budgetRequestRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetRequestVendorRepository budgetRequestVendorRepository;
    private final VendorRepository vendorRepository;
    private final VendorProductRepository vendorProductRepository;

    public BudgetRequestDistributionServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            BudgetItemRepository budgetItemRepository,
            BudgetRequestVendorRepository budgetRequestVendorRepository,
            VendorRepository vendorRepository,
            VendorProductRepository vendorProductRepository
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetRequestVendorRepository = budgetRequestVendorRepository;
        this.vendorRepository = vendorRepository;
        this.vendorProductRepository = vendorProductRepository;
    }

    @Override
    @Transactional
    public List<BudgetRequestVendor> dispatch(Long requestId) {
        BudgetRequest budgetRequest = budgetRequestRepository.findOptionalById(requestId)
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));

        List<BudgetItem> items = budgetItemRepository.findByRequestId(requestId);
        List<Long> productIds = items.stream()
                .filter(item -> item.getProduct() != null)
                .map(item -> item.getProduct().getId())
                .distinct()
                .toList();

        if (productIds.isEmpty()) {
            throw new WebApplicationException("Budget request has no mapped products to distribute", Response.Status.CONFLICT);
        }

        List<Vendor> activeVendors = vendorRepository.findAllActive();
        List<BudgetRequestVendor> assignments = activeVendors.stream()
                .filter(vendor -> vendorProductRepository.findProductIdsByVendorId(vendor.getId())
                        .stream()
                        .anyMatch(productIds::contains))
                .map(vendor -> budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                        .orElseGet(() -> createAssignment(budgetRequest, vendor)))
                .toList();

        if (assignments.isEmpty()) {
            throw new WebApplicationException("No eligible vendors found for this request", Response.Status.CONFLICT);
        }

        budgetRequest.setStatus(BudgetRequestStatus.SENT_TO_VENDORS);
        budgetRequestRepository.save(budgetRequest);
        return assignments;
    }

    @Override
    public List<BudgetRequestVendor> findByRequestId(Long requestId) {
        return budgetRequestVendorRepository.findByRequestId(requestId);
    }

    @Override
    public BudgetRequestVendor findAssignmentForVendor(Long requestId, String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new WebApplicationException("Vendor profile not found for current user", Response.Status.NOT_FOUND));

        return budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> new WebApplicationException("Vendor is not assigned to this request", Response.Status.NOT_FOUND));
    }

    @Override
    public List<Long> findAssignedRequestIdsForVendor(String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new WebApplicationException("Vendor profile not found for current user", Response.Status.NOT_FOUND));

        return budgetRequestVendorRepository.findByVendorId(vendor.getId())
                .stream()
                .map(assignment -> assignment.getRequest().getId())
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    public void markViewed(Long requestId, String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new WebApplicationException("Vendor profile not found for current user", Response.Status.NOT_FOUND));

        BudgetRequestVendor assignment = budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> new WebApplicationException("Vendor is not assigned to this request", Response.Status.CONFLICT));

        if (assignment.getStatus() == BudgetRequestVendorStatus.SENT) {
            assignment.setStatus(BudgetRequestVendorStatus.VIEWED);
            assignment.setViewedAt(LocalDateTime.now());
            budgetRequestVendorRepository.save(assignment);
        }
    }

    @Override
    @Transactional
    public void markResponded(Long requestId, Long vendorId) {
        BudgetRequestVendor assignment = budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendorId)
                .orElseThrow(() -> new WebApplicationException("Vendor is not assigned to this request", Response.Status.CONFLICT));

        if (assignment.getStatus() == BudgetRequestVendorStatus.DECLINED) {
            throw new WebApplicationException("Vendor has already declined this request", Response.Status.CONFLICT);
        }

        assignment.setStatus(BudgetRequestVendorStatus.RESPONDED);
        if (assignment.getViewedAt() == null) {
            assignment.setViewedAt(LocalDateTime.now());
        }
        assignment.setRespondedAt(LocalDateTime.now());
        budgetRequestVendorRepository.save(assignment);

        BudgetRequest budgetRequest = assignment.getRequest();
        budgetRequest.setStatus(BudgetRequestStatus.WAITING_QUOTES);
        budgetRequestRepository.save(budgetRequest);
    }

    @Override
    @Transactional
    public void markDeclined(Long requestId, String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new WebApplicationException("Vendor profile not found for current user", Response.Status.NOT_FOUND));

        BudgetRequestVendor assignment = budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> new WebApplicationException("Vendor is not assigned to this request", Response.Status.CONFLICT));

        if (assignment.getStatus() == BudgetRequestVendorStatus.RESPONDED) {
            throw new WebApplicationException("Vendor has already responded to this request", Response.Status.CONFLICT);
        }

        assignment.setStatus(BudgetRequestVendorStatus.DECLINED);
        if (assignment.getViewedAt() == null) {
            assignment.setViewedAt(LocalDateTime.now());
        }
        assignment.setDeclinedAt(LocalDateTime.now());
        budgetRequestVendorRepository.save(assignment);
    }

    private BudgetRequestVendor createAssignment(BudgetRequest budgetRequest, Vendor vendor) {
        BudgetRequestVendor assignment = new BudgetRequestVendor();
        assignment.setRequest(budgetRequest);
        assignment.setVendor(vendor);
        assignment.setStatus(BudgetRequestVendorStatus.SENT);
        assignment.setSentAt(LocalDateTime.now());
        return budgetRequestVendorRepository.save(assignment);
    }
}
