package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.VendorController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.vendor.VendorRequest;
import br.com.uatz.server.dto.vendor.VendorResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.VendorMapping;
import br.com.uatz.server.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class VendorControllerImpl implements VendorController {

    @Inject
    VendorService vendorService;

    @Inject
    JsonWebToken jsonWebToken;

    @Override
    public Response create(VendorRequest request) {
        VendorResponse response = VendorMapping.toResponse(vendorService.create(request));
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    public List<VendorResponse> findAllActive() {
        return vendorService.findAllActive()
                .stream()
                .map(VendorMapping::toResponse)
                .toList();
    }

    @Override
    @RolesAllowed(Perfil.ADMIN)
    public List<VendorResponse> findAll() {
        return vendorService.findAll()
                .stream()
                .map(VendorMapping::toResponse)
                .toList();
    }

    @Override
    @RolesAllowed(Perfil.VENDOR)
    public VendorResponse findCurrentVendor() {
        String email = jsonWebToken.getName();
        return vendorService.findByEmail(email)
                .map(VendorMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));
    }

    @Override
    public VendorResponse findById(Long id) {
        return vendorService.findById(id)
                .map(VendorMapping::toResponse)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));
    }

    @Override
    @RolesAllowed(Perfil.ADMIN)
    public VendorResponse update(Long id, VendorRequest request) {
        return VendorMapping.toResponse(vendorService.update(id, request));
    }
}
