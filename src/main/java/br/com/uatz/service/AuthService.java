package br.com.uatz.service;

import br.com.uatz.api.dto.auth.AuthBootstrapRequest;
import br.com.uatz.api.dto.auth.AuthLoginRequest;
import br.com.uatz.api.dto.auth.AuthResponse;

public interface AuthService {

    AuthResponse bootstrapAdmin(AuthBootstrapRequest request);

    AuthResponse login(AuthLoginRequest request);
}

