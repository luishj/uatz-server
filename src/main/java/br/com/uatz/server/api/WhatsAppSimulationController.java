package br.com.uatz.server.api;

import br.com.uatz.server.dto.whatsapp.WhatsAppSimulationRequest;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/whatsapp/simulations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface WhatsAppSimulationController {

    @POST
    public abstract Response simulate(@Valid WhatsAppSimulationRequest request);
}
