package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.VendorQuoteController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.vendorquote.VendorQuoteDetailsResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.server.dto.vendorquote.VendorQuoteResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.VendorQuoteMapping;
import br.com.uatz.server.service.VendorQuoteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
public class VendorQuoteControllerImpl implements VendorQuoteController {

    @Inject
    VendorQuoteService vendorQuoteService;

    @Inject
    JsonWebToken jsonWebToken;

    @Override
    public Response create(VendorQuoteRequest request) {
        VendorQuoteResponse response = VendorQuoteMapping.toResponse(vendorQuoteService.create(request));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    public VendorQuoteResponse findById(Long id) {
        return vendorQuoteService.findById(id)
                .map(VendorQuoteMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.COTACAO_NAO_ENCONTRADA, Status.NOT_FOUND));
    }

    @Override
    public List<VendorQuoteResponse> findByRequestId(Long requestId) {
        return vendorQuoteService.findByRequestId(requestId)
                .stream()
                .map(VendorQuoteMapping::toResponse)
                .toList();
    }

    @Override
    public VendorQuoteSummaryResponse summarizeByRequestId(Long requestId) {
        return vendorQuoteService.summarizeByRequestId(requestId);
    }

    @Override
    @RolesAllowed(Perfil.VENDOR)
    public VendorQuoteDetailsResponse findCurrentVendorQuoteByRequestId(Long requestId) {
        return vendorQuoteService.findByRequestIdAndVendorEmail(requestId, jsonWebToken.getName())
                .map(VendorQuoteMapping::toDetailsResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.COTACAO_USUARIO_NAO_ENCONTRADA, Status.NOT_FOUND));
    }

    @Override
    public List<VendorQuoteResponse> findByVendorId(Long vendorId) {
        return vendorQuoteService.findByVendorId(vendorId)
                .stream()
                .map(VendorQuoteMapping::toResponse)
                .toList();
    }
}
