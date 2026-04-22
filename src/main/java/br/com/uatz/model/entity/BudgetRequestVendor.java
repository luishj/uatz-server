package br.com.uatz.model.entity;

import br.com.uatz.model.enums.BudgetRequestVendorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_request_vendors")
public class BudgetRequestVendor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private BudgetRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "declined_at")
    private LocalDateTime declinedAt;

    public BudgetRequest getRequest() {
        return request;
    }

    public void setRequest(BudgetRequest request) {
        this.request = request;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public BudgetRequestVendorStatus getStatus() {
        return BudgetRequestVendorStatus.valueOf(status.getCode());
    }

    public StatusEntity getStatusEntity() {
        return status;
    }

    public void setStatusEntity(StatusEntity status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public LocalDateTime getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(LocalDateTime declinedAt) {
        this.declinedAt = declinedAt;
    }
}
