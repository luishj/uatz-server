package br.com.uatz.model.entity;

import br.com.uatz.model.enums.BudgetRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_requests")
public class BudgetRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(length = 120)
    private String city;

    @Column(name = "source_channel", length = 30)
    private String sourceChannel;

    @Column(name = "source_message", columnDefinition = "TEXT")
    private String sourceMessage;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getSourceMessage() {
        return sourceMessage;
    }

    public void setSourceMessage(String sourceMessage) {
        this.sourceMessage = sourceMessage;
    }

    public BudgetRequestStatus getStatus() {
        return BudgetRequestStatus.valueOf(status.getCode());
    }

    public StatusEntity getStatusEntity() {
        return status;
    }

    public void setStatusEntity(StatusEntity status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
