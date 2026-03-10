package br.com.uatz.api.resource;

import br.com.uatz.api.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.api.dto.vendorquote.VendorQuoteResponse;
import br.com.uatz.api.dto.vendorquote.VendorQuoteSummaryResponse;
import br.com.uatz.api.mapper.VendorQuoteApiMapper;
import br.com.uatz.service.VendorQuoteService;
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

@Path("/api/vendor-quotes")
@RolesAllowed({"ADMIN", "OPERATOR", "VENDOR"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendorQuoteResource {

    private final VendorQuoteService vendorQuoteService;

    public VendorQuoteResource(VendorQuoteService vendorQuoteService) {
        this.vendorQuoteService = vendorQuoteService;
    }

    @POST
    public Response create(@Valid VendorQuoteRequest request) {
        VendorQuoteResponse response = VendorQuoteApiMapper.toResponse(vendorQuoteService.create(request));
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public VendorQuoteResponse findById(@PathParam("id") Long id) {
        return vendorQuoteService.findById(id)
                .map(VendorQuoteApiMapper::toResponse)
                .orElseThrow(() -> new WebApplicationException("Vendor quote not found", Response.Status.NOT_FOUND));
    }

    @GET
    @Path("/request/{requestId}")
    public List<VendorQuoteResponse> findByRequestId(@PathParam("requestId") Long requestId) {
        return vendorQuoteService.findByRequestId(requestId)
                .stream()
                .map(VendorQuoteApiMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/request/{requestId}/summary")
    public VendorQuoteSummaryResponse summarizeByRequestId(@PathParam("requestId") Long requestId) {
        return vendorQuoteService.summarizeByRequestId(requestId);
    }

    @GET
    @Path("/vendor/{vendorId}")
    public List<VendorQuoteResponse> findByVendorId(@PathParam("vendorId") Long vendorId) {
        return vendorQuoteService.findByVendorId(vendorId)
                .stream()
                .map(VendorQuoteApiMapper::toResponse)
                .toList();
    }
}
