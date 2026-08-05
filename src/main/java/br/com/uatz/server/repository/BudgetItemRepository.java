package br.com.uatz.server.repository;

import br.com.uatz.model.BudgetItem;
import java.util.List;

public interface BudgetItemRepository extends GenericRepository<BudgetItem, Long> {

    BudgetItem save(BudgetItem item);

    void deleteByRequestId(Long requestId);

    List<BudgetItem> findByRequestId(Long requestId);

    List<BudgetItem> findByRequestIds(List<Long> requestIds);
}
