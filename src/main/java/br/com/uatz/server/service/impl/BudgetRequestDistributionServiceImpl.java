package br.com.uatz.server.service.impl;

import br.com.uatz.model.BudgetItem;
import br.com.uatz.model.BudgetRequest;
import br.com.uatz.model.BudgetRequestVendor;
import br.com.uatz.model.StatusEntity;
import br.com.uatz.model.Vendor;
import br.com.uatz.model.enumerador.BudgetRequestStatus;
import br.com.uatz.model.enumerador.BudgetRequestVendorStatus;
import br.com.uatz.model.enumerador.StatusType;
import br.com.uatz.server.repository.BudgetItemRepository;
import br.com.uatz.server.repository.BudgetRequestRepository;
import br.com.uatz.server.repository.BudgetRequestVendorRepository;
import br.com.uatz.server.repository.StatusRepository;
import br.com.uatz.server.repository.VendorProductRepository;
import br.com.uatz.server.repository.VendorRepository;
import br.com.uatz.server.service.BudgetRequestDistributionService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND));

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
            throw MessageBuilder.build(CloudMessage.NENHUM_FORNECEDOR_ELEGIVEL, Status.CONFLICT);
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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        return budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_VINCULADO_PEDIDO, Status.NOT_FOUND));
    }

    @Override
    public List<Long> findAssignedRequestIdsForVendor(String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        BudgetRequestVendor assignment = budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_VINCULADO_PEDIDO, Status.CONFLICT));

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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_VINCULADO_PEDIDO, Status.CONFLICT));

        if (assignment.getStatus() == BudgetRequestVendorStatus.DECLINED) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_JA_RECUSOU_PEDIDO, Status.CONFLICT);
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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        BudgetRequestVendor assignment = budgetRequestVendorRepository.findByRequestIdAndVendorId(requestId, vendor.getId())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_VINCULADO_PEDIDO, Status.CONFLICT));

        if (assignment.getStatus() == BudgetRequestVendorStatus.RESPONDED) {
            throw MessageBuilder.build(CloudMessage.FORNECEDOR_JA_RESPONDEU_PEDIDO, Status.CONFLICT);
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
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.SITUACAO_NAO_ENCONTRADA, Status.INTERNAL_SERVER_ERROR));
    }

    private StatusEntity resolveVendorAssignmentStatus(BudgetRequestVendorStatus status) {
        return statusRepository.findByTypeAndCode(StatusType.BUDGET_REQUEST_VENDOR, status.name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.SITUACAO_NAO_ENCONTRADA, Status.INTERNAL_SERVER_ERROR));
    }
}
