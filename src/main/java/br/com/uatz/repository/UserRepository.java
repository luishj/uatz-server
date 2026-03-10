package br.com.uatz.repository;

import br.com.uatz.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findOptionalById(Long id);

    Optional<User> findByEmail(String email);

    List<User> listAllUsers();

    long countAll();
}
