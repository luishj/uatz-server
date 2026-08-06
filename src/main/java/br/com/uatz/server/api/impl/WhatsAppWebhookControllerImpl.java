package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.WhatsAppWebhookController;
import br.com.uatz.server.service.WhatsAppWebhookService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@PermitAll
public class WhatsAppWebhookControllerImpl implements WhatsAppWebhookController {

    @Inject
    WhatsAppWebhookService whatsAppWebhookService;

    @Override
    public Response verify(String mode, String token, String challenge) {
        return Response.ok(whatsAppWebhookService.verify(mode, token, challenge)).build();
    }

    @Override
    public Response receive(String signature, String payload) {
        whatsAppWebhookService.process(signature, payload);
        return Response.ok().build();
    }
}
