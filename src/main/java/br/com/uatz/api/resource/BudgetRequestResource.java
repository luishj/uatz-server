package br.com.uatz.api.resource;

import br.com.uatz.api.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.service.BudgetRequestService;
import jakarta.annotation.security.RolesAllowed;
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

@Path("/api/budget-requests")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BudgetRequestResource {

    private final BudgetRequestService budgetRequestService;

    public BudgetRequestResource(BudgetRequestService budgetRequestService) {
        this.budgetRequestService = budgetRequestService;
    }

    @POST
    public Response create(@Valid BudgetRequestCreateRequest request) {
        BudgetRequestResponse response = budgetRequestService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<BudgetRequestResponse> findAll() {
        return budgetRequestService.findAllWithItems();
    }

    @GET
    @Path("/{id}")
    public BudgetRequestResponse findById(@PathParam("id") Long id) {
        return budgetRequestService.findResponseById(id)
                .orElseThrow(() -> new WebApplicationException("Budget request not found", Response.Status.NOT_FOUND));
    }
}
