package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.WhatsAppSimulationController;
import br.com.uatz.server.constante.Perfil;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.server.dto.whatsapp.WhatsAppSimulationRequest;
import br.com.uatz.server.service.BudgetRequestService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RolesAllowed({Perfil.ADMIN, Perfil.OPERATOR})
public class WhatsAppSimulationControllerImpl implements WhatsAppSimulationController {

    @Inject
    BudgetRequestService budgetRequestService;

    @Override
    public Response simulate(WhatsAppSimulationRequest request) {
        BudgetRequestResponse response = budgetRequestService.createFromWhatsAppSimulation(request);
        return Response.status(Status.CREATED).entity(response).build();
    }
}
