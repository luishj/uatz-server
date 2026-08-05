package br.com.uatz.server.repository;

import java.util.List;

import br.com.uatz.model.VendorProduct;

public interface VendorProductRepository extends GenericRepository<VendorProduct, Long> {

    List<Long> findProductIdsByVendorId(Long vendorId);
}
