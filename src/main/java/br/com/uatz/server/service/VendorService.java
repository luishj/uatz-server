package br.com.uatz.server.service;

import br.com.uatz.model.Vendor;
import br.com.uatz.server.dto.vendor.VendorRequest;
import java.util.List;
import java.util.Optional;

public interface VendorService {

    Vendor save(Vendor vendor);

    Vendor create(VendorRequest request);

    Vendor update(Long id, VendorRequest request);

    Optional<Vendor> findById(Long id);

    Optional<Vendor> findByEmail(String email);

    List<Vendor> findAll();

    List<Vendor> findAllActive();
}
