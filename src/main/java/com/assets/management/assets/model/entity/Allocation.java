package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
// @DynamicInsert
@Table(name = "asset_allocations")
@NamedQueries({
        @NamedQuery(
                name = "Allocation.preview",
                query = " FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
                        + "LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE e.id = :employeeId AND ast.serialNumber = :serialNumber "
                        + "AND (:status IS NULL OR :status MEMBER OF a.status)"),
        @NamedQuery(
                name = "Allocation.details",
                query = " FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
                        + "LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE a.id = :allocationId")
})
@Schema(description = "Allocation representation")
public class Allocation extends PanacheEntity {

    @CreationTimestamp
    @Column(name = "allocation_date", nullable = false)
    public LocalDate allocationDate;

    @Column(name = "deallocation_date")
    public Instant deallocationDate;

    @Column(name = "allocation_remarks", length = 4000)
    public String allocationRemark;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JoinColumn(
            name = "allocation_status",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "allocation_status_fk_constraint",
                    foreignKeyDefinition = ""))
    public Set<AllocationStatus> status = new HashSet<>(List.of(AllocationStatus.ALLOCATED));

    @NotNull
    @JoinColumn(
            name = "employee_fk",
            foreignKey = @ForeignKey(
                    name = "employee_allocation_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee employee;

    @NotNull
    @JoinColumn(
            name = "asset_fk",
            foreignKey = @ForeignKey(
                    name = "asset_allocation_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Asset asset;

    public static PanacheQuery<Allocation> listAll(AllocationStatus filteredStatus, Long employeeId) {
        return find("FROM Allocation a WHERE a.employee.id = :employeeId AND (:status IS NULL OR :status MEMBER OF a.status)",
                Parameters.with("employeeId", employeeId).and("status", filteredStatus));
    }

    public static Allocation preview(Long employeeId, String serialNumber) {
        return find("#Allocation.preview",
                Parameters.with("employeeId", employeeId).and("serialNumber", serialNumber)
                        .and("status", AllocationStatus.ALLOCATED)
        ).firstResult();
    }

    public static Allocation qrDetails(Long allocationId) {
        return find("#Allocation.details", Parameters.with("allocationId", allocationId))
                .firstResult();
    }

}
