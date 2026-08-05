package br.com.uatz.server.api;

import br.com.uatz.server.dto.client.ClientRequest;
import br.com.uatz.server.dto.client.ClientResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface ClientController {

    @POST
    public abstract Response create(@Valid ClientRequest request);

    @GET
    @Path("/all")
    public abstract List<ClientResponse> findAll();

    @GET
    @Path("/{id}")
    public abstract ClientResponse findById(@PathParam("id") Long id);
}
