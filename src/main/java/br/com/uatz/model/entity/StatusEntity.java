package br.com.uatz.model.entity;

import br.com.uatz.model.enums.StatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "statuses")
public class StatusEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusType type;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 80)
    private String name;

    public StatusType getType() {
        return type;
    }

    public void setType(StatusType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
