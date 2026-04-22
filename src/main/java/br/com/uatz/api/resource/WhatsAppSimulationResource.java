package br.com.uatz.api.resource;

import br.com.uatz.api.dto.budget.BudgetRequestResponse;
import br.com.uatz.api.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.service.BudgetRequestService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/whatsapp/simulations")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WhatsAppSimulationResource {

    private final BudgetRequestService budgetRequestService;

    public WhatsAppSimulationResource(BudgetRequestService budgetRequestService) {
        this.budgetRequestService = budgetRequestService;
    }

    @POST
    public Response simulate(@Valid WhatsAppSimulationRequest request) {
        BudgetRequestResponse response = budgetRequestService.createFromWhatsAppSimulation(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
