package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.VendorQuote;
import br.com.uatz.repository.VendorQuoteRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorQuoteRepositoryImpl implements VendorQuoteRepository, PanacheRepositoryBase<VendorQuote, Long> {

    @Override
    @Transactional
    public VendorQuote save(VendorQuote vendorQuote) {
        persist(vendorQuote);
        return vendorQuote;
    }

    @Override
    public Optional<VendorQuote> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<VendorQuote> findByRequestIdAndVendorId(Long requestId, Long vendorId) {
        return find("request.id = ?1 and vendor.id = ?2", requestId, vendorId).firstResultOptional();
    }

    @Override
    public List<VendorQuote> findByRequestId(Long requestId) {
        return find("request.id", requestId).list();
    }

    @Override
    public List<VendorQuote> findByVendorId(Long vendorId) {
        return find("vendor.id", vendorId).list();
    }
}
