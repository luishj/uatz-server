package br.com.uatz.server.repository.impl;

import br.com.uatz.model.BudgetItem;
import br.com.uatz.server.repository.BudgetItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class BudgetItemRepositoryImpl extends GenericRepositoryImpl<BudgetItem, Long> implements BudgetItemRepository {

    @Override
    @Transactional
    public BudgetItem save(BudgetItem item) {
        persist(item);
        return item;
    }

    @Override
    @Transactional
    public void deleteByRequestId(Long requestId) {
        delete("request.id", requestId);
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
