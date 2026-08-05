package br.com.uatz.server.service;

import br.com.uatz.server.dto.user.UserCreateRequest;
import br.com.uatz.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User create(UserCreateRequest request);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    long countAll();
}

