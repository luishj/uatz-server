package br.com.uatz.service.impl;

import br.com.uatz.api.dto.user.UserCreateRequest;
import br.com.uatz.model.entity.User;
import br.com.uatz.repository.UserRepository;
import br.com.uatz.service.PasswordService;
import br.com.uatz.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserServiceImpl(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User create(UserCreateRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new WebApplicationException("Email already exists", Response.Status.CONFLICT);
        });

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordService.hash(request.password()));
        user.setRole(request.role());
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findOptionalById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.listAllUsers();
    }

    @Override
    public long countAll() {
        return userRepository.countAll();
    }
}
