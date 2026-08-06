package br.com.uatz.server.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Webhook da WhatsApp Cloud API. É público: quem chama é a Meta, que se
 * identifica pelo token de verificação no cadastro da URL e pela assinatura
 * HMAC em cada notificação.
 */
@Path("/api/whatsapp/webhook")
@RegisterForReflection
public interface WhatsAppWebhookController {

    /**
     * Verificação feita uma vez, no cadastro da URL: a Meta espera receber de
     * volta o valor de {@code hub.challenge} em texto puro.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public abstract Response verify(
            @QueryParam("hub.mode") String mode,
            @QueryParam("hub.verify_token") String token,
            @QueryParam("hub.challenge") String challenge
    );

    /**
     * Recebe as notificações. O payload chega como texto porque a assinatura
     * do header é calculada sobre o corpo exato — desserializar antes de
     * conferir tornaria a validação impossível, então a leitura do JSON e a
     * validação do conteúdo ficam no serviço.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response receive(
            @HeaderParam("X-Hub-Signature-256") String signature,
            String payload
    );
}
