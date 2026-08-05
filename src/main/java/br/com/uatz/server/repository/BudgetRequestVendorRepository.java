package br.com.uatz.server.repository;

import br.com.uatz.model.BudgetRequestVendor;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestVendorRepository extends GenericRepository<BudgetRequestVendor, Long> {

    BudgetRequestVendor save(BudgetRequestVendor budgetRequestVendor);

    Optional<BudgetRequestVendor> findByRequestIdAndVendorId(Long requestId, Long vendorId);

    List<BudgetRequestVendor> findByRequestId(Long requestId);

    List<BudgetRequestVendor> findByVendorId(Long vendorId);
}
