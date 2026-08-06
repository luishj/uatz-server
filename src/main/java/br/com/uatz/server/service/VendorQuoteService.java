package br.com.uatz.server.service;

import br.com.uatz.server.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.model.VendorQuote;
import java.util.List;
import java.util.Optional;

/**
 * Cotação é dado competitivo: o preço de um fornecedor não pode chegar ao
 * concorrente. Por isso os métodos existem em par — a versão sem sufixo é para
 * ADMIN/OPERATOR, que enxergam o pedido inteiro, e a versão {@code ForVendor}
 * confere a posse pelo e-mail do token antes de devolver qualquer coisa.
 */
public interface VendorQuoteService {

    VendorQuote create(VendorQuoteRequest request);

    /**
     * Só deixa o fornecedor cotar em nome dele mesmo: o {@code vendorId} do corpo
     * é conferido contra o vendedor dono do e-mail do token.
     */
    VendorQuote createForVendor(VendorQuoteRequest request, String vendorEmail);

    Optional<VendorQuote> findById(Long id);

    Optional<VendorQuote> findByIdForVendor(Long id, String vendorEmail);

    List<VendorQuote> findByRequestId(Long requestId);

    List<VendorQuote> findByVendorId(Long vendorId);

    List<VendorQuote> findByVendorIdForVendor(Long vendorId, String vendorEmail);

    VendorQuoteSummaryResponse summarizeByRequestId(Long requestId);

    Optional<VendorQuote> findByRequestIdAndVendorEmail(Long requestId, String email);
}
