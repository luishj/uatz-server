package br.com.uatz.server.api;

import br.com.uatz.server.dto.StatusResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/status")
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface StatusController {

    @GET
    public abstract StatusResponse status();
}
