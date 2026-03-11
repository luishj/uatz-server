package br.com.uatz.api.resource;

import br.com.uatz.api.dto.client.ClientRequest;
import br.com.uatz.api.dto.client.ClientResponse;
import br.com.uatz.api.mapper.ClientApiMapper;
import br.com.uatz.service.ClientService;
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

@Path("/api/clients")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

    private final ClientService clientService;

    public ClientResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @POST
    public Response create(@Valid ClientRequest request) {
        ClientResponse response = ClientApiMapper.toResponse(clientService.save(ClientApiMapper.toEntity(request)));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/all")
    @RolesAllowed("ADMIN")
    public List<ClientResponse> findAll() {
        return clientService.findAll()
                .stream()
                .map(ClientApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public ClientResponse findById(@PathParam("id") Long id) {
        return clientService.findById(id)
                .map(ClientApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("Client not found", Response.Status.NOT_FOUND));
    }
}
