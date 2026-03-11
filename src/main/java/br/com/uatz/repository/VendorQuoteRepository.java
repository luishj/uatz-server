package br.com.uatz.repository;

import br.com.uatz.model.entity.VendorQuote;
import java.util.List;
import java.util.Optional;

public interface VendorQuoteRepository {

    VendorQuote save(VendorQuote vendorQuote);

    Optional<VendorQuote> findOptionalById(Long id);

    Optional<VendorQuote> findByRequestIdAndVendorId(Long requestId, Long vendorId);

    List<VendorQuote> findByRequestId(Long requestId);

    List<VendorQuote> findByVendorId(Long vendorId);
}
