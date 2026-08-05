package br.com.uatz.server.repository.impl;

import br.com.uatz.model.VendorQuote;
import br.com.uatz.server.repository.VendorQuoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorQuoteRepositoryImpl extends GenericRepositoryImpl<VendorQuote, Long> implements VendorQuoteRepository {

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
