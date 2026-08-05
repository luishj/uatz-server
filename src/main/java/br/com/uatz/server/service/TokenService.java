package br.com.uatz.server.service;

import br.com.uatz.server.dto.auth.AuthResponse;
import br.com.uatz.model.User;

public interface TokenService {

    AuthResponse generate(User user);
}

