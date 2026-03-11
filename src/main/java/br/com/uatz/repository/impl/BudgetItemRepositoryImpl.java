package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.BudgetItem;
import br.com.uatz.repository.BudgetItemRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class BudgetItemRepositoryImpl implements BudgetItemRepository, PanacheRepositoryBase<BudgetItem, Long> {

    @Override
    @Transactional
    public BudgetItem save(BudgetItem item) {
        persist(item);
        return item;
    }

    @Override
    public List<BudgetItem> findByRequestId(Long requestId) {
        return find("request.id", requestId).list();
    }

    @Override
    public List<BudgetItem> findByRequestIds(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return List.of();
        }

        return find("request.id in ?1", requestIds).list();
    }
}
