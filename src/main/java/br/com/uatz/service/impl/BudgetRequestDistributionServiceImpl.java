package br.com.uatz.service.impl;

import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.BudgetRequestVendor;
import br.com.uatz.model.entity.StatusEntity;
import br.com.uatz.model.entity.Vendor;
import br.com.uatz.model.enums.BudgetRequestStatus;
import br.com.uatz.model.enums.BudgetRequestVendorStatus;
import br.com.uatz.model.enums.StatusType;
import br.com.uatz.repository.BudgetItemRepository;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.BudgetRequestVendorRepository;
import br.com.uatz.repository.StatusRepository;
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
    private final StatusRepository statusRepository;

    public BudgetRequestDistributionServiceImpl(
            BudgetRequestRepository budgetRequestRepository,
            BudgetItemRepository budgetItemRepository,
            BudgetRequestVendorRepository budgetRequestVendorRepository,
            VendorRepository vendorRepository,
            VendorProductRepository vendorProductRepository,
            StatusRepository statusRepository
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetRequestVendorRepository = budgetRequestVendorRepository;
        this.vendorRepository = vendorRepository;
        this.vendorProductRepository = vendorProductRepository;
        this.statusRepository = statusRepository;
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

        List<Vendor> activeVendors = vendorRepository.findAllActive();
        List<Vendor> eligibleVendors = productIds.isEmpty()
                ? activeVendors
                : activeVendors.stream()
                .filter(vendor -> vendorProductRepository.findProductIdsByVendorId(vendor.getId())
                        .stream()
                        .anyMatch(productIds::contains))
                .toList();

        List<BudgetRequestVendor> assignments = eligibleVendors.stream()
                .map(vendor -> budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                        .orElseGet(() -> createAssignment(budgetRequest, vendor)))
                .toList();

        if (assignments.isEmpty()) {
            throw new WebApplicationException("No eligible vendors found for this request", Response.Status.CONFLICT);
        }

        budgetRequest.setStatusEntity(resolveBudgetRequestStatus(BudgetRequestStatus.SENT_TO_VENDORS));
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
            assignment.setStatusEntity(resolveVendorAssignmentStatus(BudgetRequestVendorStatus.VIEWED));
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

        assignment.setStatusEntity(resolveVendorAssignmentStatus(BudgetRequestVendorStatus.RESPONDED));
        if (assignment.getViewedAt() == null) {
            assignment.setViewedAt(LocalDateTime.now());
        }
        assignment.setRespondedAt(LocalDateTime.now());
        budgetRequestVendorRepository.save(assignment);

        BudgetRequest budgetRequest = assignment.getRequest();
        budgetRequest.setStatusEntity(resolveBudgetRequestStatus(BudgetRequestStatus.WAITING_QUOTES));
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

        assignment.setStatusEntity(resolveVendorAssignmentStatus(BudgetRequestVendorStatus.DECLINED));
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
        assignment.setStatusEntity(resolveVendorAssignmentStatus(BudgetRequestVendorStatus.SENT));
        assignment.setSentAt(LocalDateTime.now());
        return budgetRequestVendorRepository.save(assignment);
    }

    private StatusEntity resolveBudgetRequestStatus(BudgetRequestStatus status) {
        return statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST, status.name())
                .orElseThrow(() -> new WebApplicationException("Status not found", Response.Status.INTERNAL_SERVER_ERROR));
    }

    private StatusEntity resolveVendorAssignmentStatus(BudgetRequestVendorStatus status) {
        return statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST_VENDOR, status.name())
                .orElseThrow(() -> new WebApplicationException("Status not found", Response.Status.INTERNAL_SERVER_ERROR));
    }
}
