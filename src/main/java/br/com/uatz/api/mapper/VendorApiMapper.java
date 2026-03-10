package br.com.uatz.api.mapper;

import br.com.uatz.api.dto.vendor.VendorRequest;
import br.com.uatz.api.dto.vendor.VendorResponse;
import br.com.uatz.model.entity.Vendor;
import java.time.LocalDateTime;

public final class VendorApiMapper {

    private VendorApiMapper() {
    }

    public static Vendor toEntity(VendorRequest request) {
        Vendor vendor = new Vendor();
        vendor.setName(request.name());
        vendor.setPhone(request.phone());
        vendor.setEmail(request.email());
        vendor.setCity(request.city());
        vendor.setState(request.state());
        vendor.setActive(request.active() != null ? request.active() : Boolean.TRUE);
        vendor.setCreatedAt(LocalDateTime.now());
        return vendor;
    }

    public static VendorResponse toResponse(Vendor vendor) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getPhone(),
                vendor.getEmail(),
                vendor.getCity(),
                vendor.getState(),
                vendor.getActive()
        );
    }
}

