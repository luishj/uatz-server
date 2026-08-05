package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.BudgetRequestController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.server.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.server.dto.budget.BudgetRequestVendorResponse;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import br.com.uatz.server.mapping.BudgetRequestVendorMapping;
import br.com.uatz.server.service.BudgetRequestDistributionService;
import br.com.uatz.server.service.BudgetRequestService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;

public class BudgetRequestControllerImpl implements BudgetRequestController {

    @Inject
    BudgetRequestService budgetRequestService;

    @Inject
    BudgetRequestDistributionService budgetRequestDistributionService;

    @Inject
    JsonWebToken jsonWebToken;

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
    public Response create(BudgetRequestCreateRequest request) {
        BudgetRequestResponse response = budgetRequestService.create(request);
        return Response.status(Status.CREATED).entity(response).build();
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
    public BudgetRequestResponse review(Long id, BudgetRequestReviewRequest request) {
        return budgetRequestService.review(id, request);
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
    public List<BudgetRequestResponse> findAll() {
        return isVendor()
                ? budgetRequestService.findAllWithItemsForVendor(jsonWebToken.getName())
                : budgetRequestService.findAllWithItems();
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR, Perfil.VENDOR})
    public BudgetRequestResponse findById(Long id) {
        if (isVendor()) {
            budgetRequestDistributionService.markViewed(id, jsonWebToken.getName());
        }

        return (isVendor()
                ? budgetRequestService.findResponseByIdForVendor(id, jsonWebToken.getName())
                : budgetRequestService.findResponseById(id))
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND));
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
    public List<BudgetRequestVendorResponse> dispatch(Long id) {
        return budgetRequestDistributionService.dispatch(id)
                .stream()
                .map(BudgetRequestVendorMapping::toResponse)
                .toList();
    }

    @Override
    @RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
    public List<BudgetRequestVendorResponse> findAssignedVendors(Long id) {
        return budgetRequestDistributionService.findByRequestId(id)
                .stream()
                .map(BudgetRequestVendorMapping::toResponse)
                .toList();
    }

    @Override
    @RolesAllowed(Perfil.VENDOR)
    public BudgetRequestVendorResponse findMyAssignment(Long id) {
        return BudgetRequestVendorMapping.toResponse(
                budgetRequestDistributionService.findAssignmentForVendor(id, jsonWebToken.getName())
        );
    }

    @Override
    @RolesAllowed(Perfil.VENDOR)
    public Response decline(Long id) {
        budgetRequestDistributionService.markDeclined(id, jsonWebToken.getName());
        return Response.noContent().build();
    }

    private boolean isVendor() {
        return jsonWebToken.getGroups().contains(Perfil.VENDOR);
    }
}
