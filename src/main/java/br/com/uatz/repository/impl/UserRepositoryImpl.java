package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.User;
import br.com.uatz.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<User, Long> {

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
