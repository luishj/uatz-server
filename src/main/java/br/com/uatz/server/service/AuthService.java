package br.com.uatz.server.service;

import br.com.uatz.server.dto.auth.AuthBootstrapRequest;
import br.com.uatz.server.dto.auth.AuthLoginRequest;
import br.com.uatz.server.dto.auth.AuthResponse;

public interface AuthService {

    AuthResponse bootstrapAdmin(AuthBootstrapRequest request);

    AuthResponse login(AuthLoginRequest request);
}

