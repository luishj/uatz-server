package br.com.uatz.server.service.impl;

import br.com.uatz.server.dto.vendor.VendorRequest;
import br.com.uatz.server.mapping.VendorMapping;
import br.com.uatz.model.Vendor;
import br.com.uatz.model.User;
import br.com.uatz.model.enumerador.UserRole;
import br.com.uatz.server.repository.RoleRepository;
import br.com.uatz.server.repository.VendorRepository;
import br.com.uatz.server.service.PasswordService;
import br.com.uatz.server.service.UserService;
import br.com.uatz.server.service.VendorService;
import br.com.uatz.server.exception.CloudMessage;
import br.com.uatz.server.exception.MessageBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordService passwordService;

    public VendorServiceImpl(
            VendorRepository vendorRepository,
            RoleRepository roleRepository,
            UserService userService,
            PasswordService passwordService
    ) {
        this.vendorRepository = vendorRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public Vendor update(Long id, VendorRequest request) {
        Vendor vendor = vendorRepository.findOptionalById(id)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        String currentEmail = vendor.getEmail();
        validateCredentialsForVendor(request, false);
        VendorMapping.updateEntity(vendor, request);
        Vendor updatedVendor = vendorRepository.save(vendor);

        syncVendorUser(currentEmail, updatedVendor, request);
        return updatedVendor;
    }

    @Override
    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findOptionalById(id);
    }

    @Override
    public Optional<Vendor> findByEmail(String email) {
        return vendorRepository.findByEmail(email);
    }

    @Override
    public List<Vendor> findAll() {
        return vendorRepository.listAllVendors();
    }

    @Override
    public List<Vendor> findAllActive() {
        return vendorRepository.findAllActive();
    }

    @Override
    @Transactional
    public Vendor create(VendorRequest request) {
        validateCredentialsForVendor(request, true);

        Vendor vendor = vendorRepository.save(VendorMapping.toEntity(request));
        createVendorUser(vendor, request.password());
        return vendor;
    }

    private void validateCredentialsForVendor(VendorRequest request, boolean passwordRequired) {
        if (request.email() == null || request.email().isBlank()) {
            throw MessageBuilder.build(CloudMessage.EMAIL_FORNECEDOR_OBRIGATORIO, Status.BAD_REQUEST);
        }

        if (passwordRequired && (request.password() == null || request.password().isBlank())) {
            throw MessageBuilder.build(CloudMessage.SENHA_FORNECEDOR_OBRIGATORIA, Status.BAD_REQUEST);
        }
    }

    private void createVendorUser(Vendor vendor, String rawPassword) {
        userService.findByEmail(vendor.getEmail()).ifPresent(existingUser -> {
            throw MessageBuilder.build(CloudMessage.EMAIL_JA_EXISTE, Status.CONFLICT);
        });

        User user = new User();
        user.setName(vendor.getName());
        user.setEmail(vendor.getEmail());
        user.setPasswordHash(passwordService.hash(rawPassword));
        user.setRoleEntity(roleRepository.findByCode(UserRole.VENDOR.name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_NAO_ENCONTRADO, Status.INTERNAL_SERVER_ERROR)));
        user.setCreatedAt(vendor.getCreatedAt());
        userService.save(user);
    }

    private void syncVendorUser(String previousEmail, Vendor vendor, VendorRequest request) {
        if (previousEmail == null || previousEmail.isBlank()) {
            return;
        }

        User user = userService.findByEmail(previousEmail)
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.USUARIO_FORNECEDOR_NAO_ENCONTRADO, Status.NOT_FOUND));

        if (!previousEmail.equalsIgnoreCase(vendor.getEmail())) {
            userService.findByEmail(vendor.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw MessageBuilder.build(CloudMessage.EMAIL_JA_EXISTE, Status.CONFLICT);
                }
            });
            user.setEmail(vendor.getEmail());
        }

        user.setName(vendor.getName());
        user.setRoleEntity(roleRepository.findByCode(UserRole.VENDOR.name())
                .orElseThrow(() -> MessageBuilder.build(CloudMessage.PERFIL_NAO_ENCONTRADO, Status.INTERNAL_SERVER_ERROR)));

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordService.hash(request.password()));
        }

        userService.save(user);
    }
}
