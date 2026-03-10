package br.com.uatz.service.impl;

import br.com.uatz.api.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.api.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.api.mapper.VendorQuoteApiMapper;
import br.com.uatz.model.entity.BudgetRequest;
import br.com.uatz.model.entity.Vendor;
import br.com.uatz.model.entity.VendorQuote;
import br.com.uatz.repository.BudgetRequestRepository;
import br.com.uatz.repository.VendorQuoteRepository;
import br.com.uatz.repository.VendorRepository;
import br.com.uatz.service.VendorQuoteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorQuoteServiceImpl implements VendorQuoteService {

    private final VendorQuoteRepository vendorQuoteRepository;
    private final BudgetRequestRepository budgetRequestRepository;
    private final VendorRepository vendorRepository;

    public VendorQuoteServiceImpl(
            VendorQuoteRepository vendorQuoteRepository,
            BudgetRequestRepository budgetRequestRepository,
            VendorRepository vendorRepository
    ) {
        this.vendorQuoteRepository = vendorQuoteRepository;
        this.budgetRequestRepository = budgetRequestRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    @Transactional
    public VendorQuote create(VendorQuoteRequest request) {
        BudgetRequest budgetRequest = budgetRequestRepository.findOptionalById(request.requestId())
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));

        Vendor vendor = vendorRepository.findOptionalById(request.vendorId())
                .orElseThrow(() -> new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND));

        if (!Boolean.TRUE.equals(vendor.getActive())) {
            throw new WebApplicationException("Vendor is inactive", Response.Status.CONFLICT);
        }

        VendorQuote vendorQuote = new VendorQuote();
        vendorQuote.setRequest(budgetRequest);
        vendorQuote.setVendor(vendor);
        vendorQuote.setTotalPrice(request.totalPrice());
        vendorQuote.setMessage(request.message());
        vendorQuote.setCreatedAt(LocalDateTime.now());
        return vendorQuoteRepository.save(vendorQuote);
    }

    @Override
    public Optional<VendorQuote> findById(Long id) {
        return vendorQuoteRepository.findOptionalById(id);
    }

    @Override
    public List<VendorQuote> findByRequestId(Long requestId) {
        validateBudgetRequestExists(requestId);
        return vendorQuoteRepository.findByRequestId(requestId);
    }

    @Override
    public List<VendorQuote> findByVendorId(Long vendorId) {
        validateVendorExists(vendorId);
        return vendorQuoteRepository.findByVendorId(vendorId);
    }

    @Override
    public VendorQuoteSummaryResponse summarizeByRequestId(Long requestId) {
        validateBudgetRequestExists(requestId);
        return VendorQuoteApiMapper.toSummary(requestId, vendorQuoteRepository.findByRequestId(requestId));
    }

    private void validateBudgetRequestExists(Long requestId) {
        if (budgetRequestRepository.findOptionalById(requestId).isEmpty()) {
            throw new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND);
        }
    }

    private void validateVendorExists(Long vendorId) {
        if (vendorRepository.findOptionalById(vendorId).isEmpty()) {
            throw new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND);
        }
    }
}
