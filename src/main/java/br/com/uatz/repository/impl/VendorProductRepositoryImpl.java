package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.VendorProduct;
import br.com.uatz.repository.VendorProductRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class VendorProductRepositoryImpl implements VendorProductRepository, PanacheRepositoryBase<VendorProduct, Long> {

    @Override
    public List<Long> findProductIdsByVendorId(Long vendorId) {
        return find("vendor.id", vendorId)
                .stream()
                .map(vendorProduct -> vendorProduct.getProduct().getId())
                .toList();
    }
}
