package br.com.uatz.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String phone;

    @Column(length = 120)
    private String city;

    @Column(length = 60)
    private String state;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

