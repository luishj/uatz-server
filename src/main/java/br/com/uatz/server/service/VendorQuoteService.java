package br.com.uatz.server.service;

import br.com.uatz.server.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.model.VendorQuote;
import java.util.List;
import java.util.Optional;

public interface VendorQuoteService {

    VendorQuote create(VendorQuoteRequest request);

    Optional<VendorQuote> findById(Long id);

    List<VendorQuote> findByRequestId(Long requestId);

    List<VendorQuote> findByVendorId(Long vendorId);

    VendorQuoteSummaryResponse summarizeByRequestId(Long requestId);

    Optional<VendorQuote> findByRequestIdAndVendorEmail(Long requestId, String email);
}
