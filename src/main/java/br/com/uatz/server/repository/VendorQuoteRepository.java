package br.com.uatz.server.repository;

import br.com.uatz.model.VendorQuote;
import java.util.List;
import java.util.Optional;

public interface VendorQuoteRepository extends GenericRepository<VendorQuote, Long> {

    VendorQuote save(VendorQuote vendorQuote);

    Optional<VendorQuote> findOptionalById(Long id);

    Optional<VendorQuote> findByRequestIdAndVendorId(Long requestId, Long vendorId);

    List<VendorQuote> findByRequestId(Long requestId);

    List<VendorQuote> findByVendorId(Long vendorId);
}
