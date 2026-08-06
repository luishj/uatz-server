package br.com.uatz.server.client;

import br.com.uatz.server.dto.whatsapp.WhatsAppSendMessageRequest;
import br.com.uatz.server.dto.whatsapp.WhatsAppSendMessageResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente da WhatsApp Cloud API (Graph API). A URL base fica em
 * {@code quarkus.rest-client.whatsapp-cloud-api.url} e o token de acesso é
 * passado pelo chamador no header Authorization, porque é uma credencial de
 * runtime e não de configuração do cliente.
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "whatsapp-cloud-api")
public interface WhatsAppCloudApiClient {

    @POST
    @Path("/{phoneNumberId}/messages")
    public abstract WhatsAppSendMessageResponse sendMessage(
            @PathParam("phoneNumberId") String phoneNumberId,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
            WhatsAppSendMessageRequest request
    );

}
