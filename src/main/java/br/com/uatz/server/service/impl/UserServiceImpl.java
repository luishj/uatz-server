package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.user.UserCreateRequest;
import br.com.uatz.model.User;
import br.com.uatz.server.repository.RoleRepository;
import br.com.uatz.server.repository.UserRepository;
import br.com.uatz.server.service.PasswordService;
import br.com.uatz.server.service.UserService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
            throw MessageBuilder.build(CloudMessage.EMAIL_JA_EXISTE, Status.CONFLICT);
        });

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordService.hash(request.password()));
        user.setRoleEntity(roleRepository.findByCode(request.role().name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_NAO_ENCONTRADO, Status.INTERNAL_SERVER_ERROR)));
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
