package br.com.uatz.service;

import br.com.uatz.api.dto.auth.AuthResponse;
import br.com.uatz.model.entity.User;

public interface TokenService {

    AuthResponse generate(User user);
}

