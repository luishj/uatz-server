package br.com.uatz.server.api.impl;

import br.com.uatz.server.api.StatusController;
import br.com.uatz.server.dto.StatusResponse;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class StatusControllerImpl implements StatusController {

    @Override
    public StatusResponse status() {
        return new StatusResponse("uatz-server", "UP");
    }
}
