package br.com.uatz.api.resource;

import br.com.uatz.api.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.api.dto.budget.BudgetRequestVendorResponse;
import br.com.uatz.api.mapper.BudgetRequestVendorApiMapper;
import br.com.uatz.service.BudgetRequestDistributionService;
import br.com.uatz.service.BudgetRequestService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/budget-requests")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BudgetRequestResource {

    private final BudgetRequestService budgetRequestService;
    private final BudgetRequestDistributionService budgetRequestDistributionService;
    @Inject
    JsonWebToken jsonWebToken;

    public BudgetRequestResource(
            BudgetRequestService budgetRequestService,
            BudgetRequestDistributionService budgetRequestDistributionService
    ) {
        this.budgetRequestService = budgetRequestService;
        this.budgetRequestDistributionService = budgetRequestDistributionService;
    }

    @POST
    @RolesAllowed({"ADMIN", "OPERATOR"})
    public Response create(@Valid BudgetRequestCreateRequest request) {
        BudgetRequestResponse response = budgetRequestService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @RolesAllowed({"ADMIN", "OPERATOR", "VENDOR"})
    public List<BudgetRequestResponse> findAll() {
        return isVendor()
                ? budgetRequestService.findAllWithItemsForVendor(jsonWebToken.getName())
                : budgetRequestService.findAllWithItems();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "OPERATOR", "VENDOR"})
    public BudgetRequestResponse findById(@PathParam("id") Long id) {
        if (isVendor()) {
            budgetRequestDistributionService.markViewed(id, jsonWebToken.getName());
        }

        return (isVendor()
                ? budgetRequestService.findResponseByIdForVendor(id, jsonWebToken.getName())
                : budgetRequestService.findResponseById(id))
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));
    }

    @POST
    @Path("/{id}/dispatch")
    @RolesAllowed({"ADMIN", "OPERATOR"})
    public List<BudgetRequestVendorResponse> dispatch(@PathParam("id") Long id) {
        return budgetRequestDistributionService.dispatch(id)
                .stream()
                .map(BudgetRequestVendorApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}/vendors")
    @RolesAllowed({"ADMIN", "OPERATOR"})
    public List<BudgetRequestVendorResponse> findAssignedVendors(@PathParam("id") Long id) {
        return budgetRequestDistributionService.findByRequestId(id)
                .stream()
                .map(BudgetRequestVendorApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}/assignment/me")
    @RolesAllowed("VENDOR")
    public BudgetRequestVendorResponse findMyAssignment(@PathParam("id") Long id) {
        return BudgetRequestVendorApiMapper.toResponse(
                budgetRequestDistributionService.findAssignmentForVendor(id, jsonWebToken.getName())
        );
    }

    @POST
    @Path("/{id}/decline")
    @RolesAllowed("VENDOR")
    public Response decline(@PathParam("id") Long id) {
        budgetRequestDistributionService.markDeclined(id, jsonWebToken.getName());
        return Response.noContent().build();
    }

    private boolean isVendor() {
        return jsonWebToken.getGroups().contains("VENDOR");
    }
}
