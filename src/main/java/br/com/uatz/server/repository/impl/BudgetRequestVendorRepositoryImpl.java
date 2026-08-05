package br.com.uatz.server.repository.impl;

import br.com.uatz.model.BudgetRequestVendor;
import br.com.uatz.server.repository.BudgetRequestVendorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestVendorRepositoryImpl extends GenericRepositoryImpl<BudgetRequestVendor, Long> implements BudgetRequestVendorRepository {

    @Override
    @Transactional
    public BudgetRequestVendor save(BudgetRequestVendor budgetRequestVendor) {
        persist(budgetRequestVendor);
        return budgetRequestVendor;
    }

    @Override
    public Optional<BudgetRequestVendor> findByRequestIdAndVendorId(Long requestId, Long vendorId) {
        return find("request.id = ?1 and vendor.id = ?2", requestId, vendorId).firstResultOptional();
    }

    @Override
    public List<BudgetRequestVendor> findByRequestId(Long requestId) {
        return find("request.id", requestId).list();
    }

    @Override
    public List<BudgetRequestVendor> findByVendorId(Long vendorId) {
        return find("vendor.id", vendorId).list();
    }
}
