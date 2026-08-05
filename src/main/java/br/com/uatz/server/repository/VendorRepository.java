package br.com.uatz.server.repository;

import br.com.uatz.model.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorRepository extends GenericRepository<Vendor, Long> {

    Vendor save(Vendor vendor);

    Optional<Vendor> findOptionalById(Long id);

    Optional<Vendor> findByEmail(String email);

    List<Vendor> listAllVendors();

    List<Vendor> findAllActive();
}
