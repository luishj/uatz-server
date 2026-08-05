package br.com.uatz.server.api;

import br.com.uatz.server.dto.vendorquote.VendorQuoteDetailsResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteRequest;
import br.com.uatz.server.dto.vendorquote.VendorQuoteResponse;
import br.com.uatz.server.dto.vendorquote.VendorQuoteSummaryResponse;
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

@Path("/api/vendor-quotes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface VendorQuoteController {

    @POST
    public abstract Response create(@Valid VendorQuoteRequest request);

    @GET
    @Path("/{id}")
    public abstract VendorQuoteResponse findById(@PathParam("id") Long id);

    @GET
    @Path("/request/{requestId}")
    public abstract List<VendorQuoteResponse> findByRequestId(@PathParam("requestId") Long requestId);

    @GET
    @Path("/request/{requestId}/summary")
    public abstract VendorQuoteSummaryResponse summarizeByRequestId(@PathParam("requestId") Long requestId);

    @GET
    @Path("/request/{requestId}/me")
    public abstract VendorQuoteDetailsResponse findCurrentVendorQuoteByRequestId(@PathParam("requestId") Long requestId);

    @GET
    @Path("/vendor/{vendorId}")
    public abstract List<VendorQuoteResponse> findByVendorId(@PathParam("vendorId") Long vendorId);
}
