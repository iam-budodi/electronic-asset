package com.japhet_sebastian.employee;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public class BaseEntity {

    @CreationTimestamp
    @Column(name = "registered_at")
    private LocalDate registeredAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "registered_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    private String registeredBy;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    private String updatedBy;

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return Objects.equals(getRegisteredAt(), that.getRegisteredAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && Objects.equals(getRegisteredBy(), that.getRegisteredBy()) && Objects.equals(getUpdatedBy(), that.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegisteredAt(), getUpdatedAt(), getRegisteredBy(), getUpdatedBy());
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "registeredAt=" + registeredAt +
                ", updatedAt=" + updatedAt +
                ", registeredBy='" + registeredBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
