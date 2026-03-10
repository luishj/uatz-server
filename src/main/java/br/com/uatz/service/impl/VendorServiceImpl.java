package br.com.uatz.service.impl;

import br.com.uatz.model.entity.Vendor;
import br.com.uatz.repository.VendorRepository;
import br.com.uatz.service.VendorService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findOptionalById(id);
    }

    @Override
    public List<Vendor> findAllActive() {
        return vendorRepository.findAllActive();
    }
}
