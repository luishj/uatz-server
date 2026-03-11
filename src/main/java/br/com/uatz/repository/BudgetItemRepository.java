package br.com.uatz.repository;

import br.com.uatz.model.entity.BudgetItem;
import java.util.List;

public interface BudgetItemRepository {

    BudgetItem save(BudgetItem item);

    List<BudgetItem> findByRequestId(Long requestId);

    List<BudgetItem> findByRequestIds(List<Long> requestIds);
}
