package br.com.uatz.server.repository;

import br.com.uatz.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends GenericRepository<User, Long> {

    User save(User user);

    Optional<User> findOptionalById(Long id);

    Optional<User> findByEmail(String email);

    List<User> listAllUsers();

    long countAll();
}
