package br.com.uatz.api;

import br.com.uatz.api.dto.StatusResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/status")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource {

    @GET
    public StatusResponse status() {
        return new StatusResponse("uatz-server", "UP");
    }
}
