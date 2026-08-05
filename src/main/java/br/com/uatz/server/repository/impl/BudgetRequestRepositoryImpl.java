package br.com.uatz.server.repository.impl;

import br.com.uatz.model.BudgetRequest;
import br.com.uatz.server.repository.BudgetRequestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestRepositoryImpl extends GenericRepositoryImpl<BudgetRequest, Long> implements BudgetRequestRepository {

    @Override
    @Transactional
    public BudgetRequest save(BudgetRequest budgetRequest) {
        persist(budgetRequest);
        return budgetRequest;
    }

    @Override
    public Optional<BudgetRequest> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public List<BudgetRequest> listAllBudgetRequests() {
        return listAll();
    }
}
