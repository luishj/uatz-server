package br.com.uatz.server.repository.impl;

import br.com.uatz.model.VendorProduct;
import br.com.uatz.server.repository.VendorProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class VendorProductRepositoryImpl extends GenericRepositoryImpl<VendorProduct, Long> implements VendorProductRepository {

    @Override
    public List<Long> findProductIdsByVendorId(Long vendorId) {
        return find("vendor.id", vendorId)
                .stream()
                .map(vendorProduct -> vendorProduct.getProduct().getId())
                .toList();
    }
}
