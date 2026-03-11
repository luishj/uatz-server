package br.com.uatz.repository;

import br.com.uatz.model.entity.BudgetRequestVendor;
import java.util.List;
import java.util.Optional;

public interface BudgetRequestVendorRepository {

    BudgetRequestVendor save(BudgetRequestVendor budgetRequestVendor);

    Optional<BudgetRequestVendor> findByRequestIdAndVendorId(Long requestId, Long vendorId);

    List<BudgetRequestVendor> findByRequestId(Long requestId);

    List<BudgetRequestVendor> findByVendorId(Long vendorId);
}
