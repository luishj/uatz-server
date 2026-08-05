package br.com.uatz.server.mapping;

import br.com.uatz.server.dto.vendor.VendorRequest;
import br.com.uatz.server.dto.vendor.VendorResponse;
import br.com.uatz.model.Vendor;
import java.time.LocalDateTime;

public final class VendorMapping {

    private VendorMapping() {
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

    public static void updateEntity(Vendor vendor, VendorRequest request) {
        vendor.setName(request.name());
        vendor.setPhone(request.phone());
        vendor.setEmail(request.email());
        vendor.setCity(request.city());
        vendor.setState(request.state());
        vendor.setActive(request.active() != null ? request.active() : vendor.getActive());
    }
}
