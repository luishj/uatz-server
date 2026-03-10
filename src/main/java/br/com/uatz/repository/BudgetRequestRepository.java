package br.com.uatz.repository;

import br.com.uatz.model.entity.BudgetRequest;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestRepository {

    BudgetRequest save(BudgetRequest budgetRequest);

    Optional<BudgetRequest> findOptionalById(Long id);

    List<BudgetRequest> listAllBudgetRequests();
}
