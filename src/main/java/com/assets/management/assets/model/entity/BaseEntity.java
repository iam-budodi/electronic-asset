package com.assets.management.assets.model.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@MappedSuperclass
public class BaseEntity extends PanacheEntity {

    @CreationTimestamp
    @Column(name = "registered_at")
    public LocalDate registeredAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDate updatedAt;

//    @NotNull
//    @Column(name = "registered_by", length = 64, nullable = false) // should be uncommented
    @Column(name = "registered_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String registeredBy;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String updatedBy;
}
