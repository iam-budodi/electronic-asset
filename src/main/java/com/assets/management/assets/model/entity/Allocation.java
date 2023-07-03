package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.time.LocalDate;

@Entity
// @DynamicInsert
@Table(name = "asset_allocations")
@NamedQueries({
        @NamedQuery(
                name = "Allocation.preview",
                query = " FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
                        + "LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE e.workId = :workId AND ast.serialNumber = :serialNumber "
                        + "AND (:status IS NULL OR a.status = :status)"),
        @NamedQuery(
                name = "Allocation.details",
                query = "FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
                        + "LEFT JOIN FETCH e.status LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
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


    @Enumerated(EnumType.STRING)
//    @ElementCollection(targetClass = AllocationStatus.class)
//    @CollectionTable(name = "allocation_status",
//            joinColumns = @JoinColumn(name = "allocation_id", nullable = false,
//                    foreignKey = @ForeignKey(name = "allocation_status_fk_constraint")))
//    @Column(name = "status")
//    @Convert(converter = AllocationStatusSetConverter.class)
//    public Set<AllocationStatus> status = new HashSet<>(List.of(AllocationStatus.ALLOCATED));

    public AllocationStatus status = AllocationStatus.ALLOCATED;

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

    @Column(name = "allocated_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String allocatedBy;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String updatedBy;

    public static PanacheQuery<Allocation> listAll(AllocationStatus filteredStatus, String workId) {
        return find("SELECT a.asset, a.employee, a.status FROM Allocation a WHERE a.employee.workId = :workId AND (:status IS NULL OR a.status = :status)",
                Parameters.with("workId", workId).and("status", filteredStatus));
    }

    public static Allocation preview(String workId, String serialNumber) {
        return find("#Allocation.preview",
                Parameters.with("workId", workId).and("serialNumber", serialNumber)
                        .and("status", AllocationStatus.ALLOCATED)
        ).firstResult();
    }

    public static Allocation qrDetails(Long allocationId) {
        return find("#Allocation.details", Parameters.with("allocationId", allocationId))
                .firstResult();
    }

}
