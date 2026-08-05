package br.com.uatz.server.api;

import br.com.uatz.server.dto.budget.BudgetRequestCreateRequest;
import br.com.uatz.server.dto.budget.BudgetRequestQuoteOptionsResponse;
import br.com.uatz.server.dto.budget.BudgetRequestResponse;
import br.com.uatz.server.dto.budget.BudgetRequestReviewRequest;
import br.com.uatz.server.dto.budget.BudgetRequestSelectionRequest;
import br.com.uatz.server.dto.budget.BudgetRequestSelectionResponse;
import br.com.uatz.server.dto.budget.BudgetRequestVendorResponse;
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

@Path("/api/budget-requests")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterForReflection
public interface BudgetRequestController {

    @POST
    public abstract Response create(@Valid BudgetRequestCreateRequest request);

    @PUT
    @Path("/{id}")
    public abstract BudgetRequestResponse review(@PathParam("id") Long id, @Valid BudgetRequestReviewRequest request);

    @GET
    public abstract List<BudgetRequestResponse> findAll();

    @GET
    @Path("/{id}")
    public abstract BudgetRequestResponse findById(@PathParam("id") Long id);

    @POST
    @Path("/{id}/dispatch")
    public abstract List<BudgetRequestVendorResponse> dispatch(@PathParam("id") Long id);

    @GET
    @Path("/{id}/vendors")
    public abstract List<BudgetRequestVendorResponse> findAssignedVendors(@PathParam("id") Long id);

    @GET
    @Path("/{id}/assignment/me")
    public abstract BudgetRequestVendorResponse findMyAssignment(@PathParam("id") Long id);

    @POST
    @Path("/{id}/decline")
    public abstract Response decline(@PathParam("id") Long id);

    @POST
    @Path("/{id}/quote-options")
    public abstract BudgetRequestQuoteOptionsResponse sendQuoteOptions(@PathParam("id") Long id);

    @POST
    @Path("/{id}/selection")
    public abstract BudgetRequestSelectionResponse selectQuote(@PathParam("id") Long id, @Valid BudgetRequestSelectionRequest request);

    @GET
    @Path("/{id}/selection")
    public abstract BudgetRequestSelectionResponse findSelection(@PathParam("id") Long id);
}
