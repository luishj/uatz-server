package br.com.uatz.service;

import br.com.uatz.model.entity.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorService {

    Vendor save(Vendor vendor);

    Optional<Vendor> findById(Long id);

    List<Vendor> findAllActive();
}

