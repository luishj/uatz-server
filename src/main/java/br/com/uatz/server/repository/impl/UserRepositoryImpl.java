package br.com.uatz.server.repository.impl;

import br.com.uatz.model.User;
import br.com.uatz.server.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl extends GenericRepositoryImpl<User, Long> implements UserRepository {

    @Override
    @Transactional
    public User save(User user) {
        persist(user);
        return user;
    }

    @Override
    public Optional<User> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public List<User> listAllUsers() {
        return listAll();
    }

    @Override
    public long countAll() {
        return count();
    }
}
