package br.com.uatz.server.api.impl;

import br.com.uatz.model.VendorQuote;
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

/**
 * A cotação é o dado mais sensível do marketplace: se um fornecedor enxerga o
 * preço do concorrente, a concorrência acaba. Por isso a classe libera apenas
 * ADMIN/OPERATOR e cada método que o fornecedor precisa usar abre o perfil
 * VENDOR explicitamente — método novo nasce fechado para o fornecedor.
 *
 * <p>As visões comparativas ({@code /request/{id}} e {@code /request/{id}/summary})
 * continuam exclusivas do operador; o fornecedor tem {@code /request/{id}/me}
 * para a cotação dele.</p>
 */
@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class VendorQuoteControllerImpl implements VendorQuoteController {

    @Inject
    VendorQuoteService vendorQuoteService;

    @Inject
    JsonWebToken jsonWebToken;

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
    public Response create(VendorQuoteRequest request) {
        VendorQuote vendorQuote = isVendor()
                ? vendorQuoteService.createForVendor(request, jsonWebToken.getName())
                : vendorQuoteService.create(request);

        return Response.status(Status.CREATED).entity(VendorQuoteMapping.toResponse(vendorQuote)).build();
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
    public VendorQuoteResponse findById(Long id) {
        return (isVendor()
                ? vendorQuoteService.findByIdForVendor(id, jsonWebToken.getName())
                : vendorQuoteService.findById(id))
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
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
    public List<VendorQuoteResponse> findByVendorId(Long vendorId) {
        return (isVendor()
                ? vendorQuoteService.findByVendorIdForVendor(vendorId, jsonWebToken.getName())
                : vendorQuoteService.findByVendorId(vendorId))
                .stream()
                .map(VendorQuoteMapping::toResponse)
                .toList();
    }

    private boolean isVendor() {
        return jsonWebToken.getGroups().contains(Perfil.VENDOR);
    }
}
