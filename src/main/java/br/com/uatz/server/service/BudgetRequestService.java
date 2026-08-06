package br.com.uatz.server.service;

import br.com.uatz.server.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.server.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.server.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.model.BudgetRequest;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestService {

    BudgetRequest save(BudgetRequest budgetRequest);

    Optional<BudgetRequest> findById(Long id);

    List<BudgetRequest> findAll();

    BudgetRequestResponse create(BudgetRequestCreateRequest request);

    BudgetRequestResponse createFromWhatsAppSimulation(WhatsAppSimulationRequest request);

    /**
     * Cria o pedido a partir de uma mensagem de WhatsApp: resolve o cliente pelo
     * telefone, registra a mensagem na conversa e quebra o texto em itens. A
     * cidade e o estado são opcionais porque a Cloud API não os informa — o
     * operador completa depois pelo endpoint de revisão.
     */
    BudgetRequestResponse createFromWhatsAppMessage(String phone, String city, String state, String message);

    BudgetRequestResponse review(Long id, BudgetRequestReviewRequest request);

    Optional<BudgetRequestResponse> findResponseById(Long id);

    Optional<BudgetRequestResponse> findResponseByIdForVendor(Long id, String vendorEmail);

    List<BudgetRequestResponse> findAllWithItems();

    List<BudgetRequestResponse> findAllWithItemsForVendor(String vendorEmail);
}
