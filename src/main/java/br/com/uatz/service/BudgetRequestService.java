package br.com.uatz.service;

import br.com.uatz.api.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.api.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.api.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.model.entity.BudgetRequest;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestService {

    BudgetRequest save(BudgetRequest budgetRequest);

    Optional<BudgetRequest> findById(Long id);

    List<BudgetRequest> findAll();

    BudgetRequestResponse create(BudgetRequestCreateRequest request);

    BudgetRequestResponse createFromWhatsAppSimulation(WhatsAppSimulationRequest request);

    BudgetRequestResponse review(Long id, BudgetRequestReviewRequest request);

    Optional<BudgetRequestResponse> findResponseById(Long id);

    Optional<BudgetRequestResponse> findResponseByIdForVendor(Long id, String vendorEmail);

    List<BudgetRequestResponse> findAllWithItems();

    List<BudgetRequestResponse> findAllWithItemsForVendor(String vendorEmail);
}
