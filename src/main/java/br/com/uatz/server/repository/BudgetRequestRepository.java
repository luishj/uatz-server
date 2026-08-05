package br.com.uatz.server.repository;

import br.com.uatz.model.BudgetRequest;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestRepository extends GenericRepository<BudgetRequest, Long> {

    BudgetRequest save(BudgetRequest budgetRequest);

    Optional<BudgetRequest> findOptionalById(Long id);

    List<BudgetRequest> listAllBudgetRequests();
}
