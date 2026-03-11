package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.Vendor;
import br.com.uatz.repository.VendorRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorRepositoryImpl implements VendorRepository, PanacheRepositoryBase<Vendor, Long> {

    @Override
    @Transactional
    public Vendor save(Vendor vendor) {
        persist(vendor);
        return vendor;
    }

    @Override
    public Optional<Vendor> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<Vendor> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public List<Vendor> listAllVendors() {
        return listAll();
    }

    @Override
    public List<Vendor> findAllActive() {
        return find("active", true).list();
    }
}
