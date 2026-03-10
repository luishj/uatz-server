package br.com.uatz.api.resource;

import br.com.uatz.api.dto.vendor.VendorRequest;
import br.com.uatz.api.dto.vendor.VendorResponse;
import br.com.uatz.api.mapper.VendorApiMapper;
import br.com.uatz.service.VendorService;
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

@Path("/api/vendors")
@RolesAllowed({"ADMIN", "OPERATOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {

    private final VendorService vendorService;

    public VendorResource(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @POST
    public Response create(@Valid VendorRequest request) {
        VendorResponse response = VendorApiMapper.toResponse(vendorService.save(VendorApiMapper.toEntity(request)));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public List<VendorResponse> findAllActive() {
        return vendorService.findAllActive()
                .stream()
                .map(VendorApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public VendorResponse findById(@PathParam("id") Long id) {
        return vendorService.findById(id)
                .map(VendorApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND));
    }
}
