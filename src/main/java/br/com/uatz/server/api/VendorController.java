package br.com.uatz.server.api;

import br.com.uatz.server.dto.vendor.VendorRequest;
import br.com.uatz.server.dto.vendor.VendorResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface VendorController {

    @POST
    public abstract Response create(@Valid VendorRequest request);

    @GET
    public abstract List<VendorResponse> findAllActive();

    @GET
    @Path("/all")
    public abstract List<VendorResponse> findAll();

    @GET
    @Path("/me")
    public abstract VendorResponse findCurrentVendor();

    @GET
    @Path("/{id}")
    public abstract VendorResponse findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    public abstract VendorResponse update(@PathParam("id") Long id, @Valid VendorRequest request);
}
