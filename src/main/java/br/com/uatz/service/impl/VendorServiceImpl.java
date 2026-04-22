package br.com.uatz.service.impl;

import br.com.uatz.api.dto.vendor.VendorRequest;
import br.com.uatz.api.mapper.VendorApiMapper;
import br.com.uatz.model.entity.Vendor;
import br.com.uatz.model.entity.User;
import br.com.uatz.model.enums.UserRole;
import br.com.uatz.repository.RoleRepository;
import br.com.uatz.repository.VendorRepository;
import br.com.uatz.service.PasswordService;
import br.com.uatz.service.UserService;
import br.com.uatz.service.VendorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
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
                .orElseThrow(() -> new WebApplicationException("Vendor not found", Response.Status.NOT_FOUND));

        String currentEmail = vendor.getEmail();
        validateCredentialsForVendor(request, false);
        VendorApiMapper.updateEntity(vendor, request);
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

        Vendor vendor = vendorRepository.save(VendorApiMapper.toEntity(request));
        createVendorUser(vendor, request.password());
        return vendor;
    }

    private void validateCredentialsForVendor(VendorRequest request, boolean passwordRequired) {
        if (request.email() == null || request.email().isBlank()) {
            throw new WebApplicationException("Vendor email is required", Response.Status.BAD_REQUEST);
        }

        if (passwordRequired && (request.password() == null || request.password().isBlank())) {
            throw new WebApplicationException("Vendor password is required", Response.Status.BAD_REQUEST);
        }
    }

    private void createVendorUser(Vendor vendor, String rawPassword) {
        userService.findByEmail(vendor.getEmail()).ifPresent(existingUser -> {
            throw new WebApplicationException("Email already exists", Response.Status.CONFLICT);
        });

        User user = new User();
        user.setName(vendor.getName());
        user.setEmail(vendor.getEmail());
        user.setPasswordHash(passwordService.hash(rawPassword));
        user.setRoleEntity(roleRepository.findByCode(UserRole.VENDOR.name())
                .orElseThrow(() -> new WebApplicationException("Role not found", Response.Status.INTERNAL_SERVER_ERROR)));
        user.setCreatedAt(vendor.getCreatedAt());
        userService.save(user);
    }

    private void syncVendorUser(String previousEmail, Vendor vendor, VendorRequest request) {
        if (previousEmail == null || previousEmail.isBlank()) {
            return;
        }

        User user = userService.findByEmail(previousEmail)
                .orElseThrow(() -> new WebApplicationException("Vendor user not found", Response.Status.NOT_FOUND));

        if (!previousEmail.equalsIgnoreCase(vendor.getEmail())) {
            userService.findByEmail(vendor.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw new WebApplicationException("Email already exists", Response.Status.CONFLICT);
                }
            });
            user.setEmail(vendor.getEmail());
        }

        user.setName(vendor.getName());
        user.setRoleEntity(roleRepository.findByCode(UserRole.VENDOR.name())
                .orElseThrow(() -> new WebApplicationException("Role not found", Response.Status.INTERNAL_SERVER_ERROR)));

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordService.hash(request.password()));
        }

        userService.save(user);
    }
}
