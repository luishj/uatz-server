package br.com.uatz.model.entity;

import br.com.uatz.model.enums.BudgetRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetRequestStatus status;

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

    public BudgetRequestStatus getStatus() {
        return status;
    }

    public void setStatus(BudgetRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
