package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.repository.BudgetRequestRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestRepositoryImpl implements BudgetRequestRepository, PanacheRepositoryBase<BudgetRequest, Long> {

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
