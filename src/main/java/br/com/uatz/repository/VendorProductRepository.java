package br.com.uatz.repository;

import java.util.List;

public interface VendorProductRepository {

    List<Long> findProductIdsByVendorId(Long vendorId);
}
