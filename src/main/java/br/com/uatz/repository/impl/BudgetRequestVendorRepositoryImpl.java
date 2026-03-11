package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.BudgetRequestVendor;
import br.com.uatz.repository.BudgetRequestVendorRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BudgetRequestVendorRepositoryImpl implements BudgetRequestVendorRepository, PanacheRepositoryBase<BudgetRequestVendor, Long> {

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
