package br.com.uatz.service;

import br.com.uatz.model.entity.BudgetRequestVendor;
import java.util.List;

public interface BudgetRequestDistributionService {

    List<BudgetRequestVendor> dispatch(Long requestId);

    List<BudgetRequestVendor> findByRequestId(Long requestId);

    BudgetRequestVendor findAssignmentForVendor(Long requestId, String vendorEmail);

    List<Long> findAssignedRequestIdsForVendor(String vendorEmail);

    void markViewed(Long requestId, String vendorEmail);

    void markResponded(Long requestId, Long vendorId);

    void markDeclined(Long requestId, String vendorEmail);
}
