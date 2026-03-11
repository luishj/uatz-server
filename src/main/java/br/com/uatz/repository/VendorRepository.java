package br.com.uatz.repository;

import br.com.uatz.model.entity.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorRepository {

    Vendor save(Vendor vendor);

    Optional<Vendor> findOptionalById(Long id);

    Optional<Vendor> findByEmail(String email);

    List<Vendor> listAllVendors();

    List<Vendor> findAllActive();
}
