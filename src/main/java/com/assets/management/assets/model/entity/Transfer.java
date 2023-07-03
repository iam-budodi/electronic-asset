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
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Table(name = "asset_transfers")
@NamedQueries({
        @NamedQuery(
                name = "Transfer.preview",
                query = "FROM Transfer t LEFT JOIN FETCH t.employee fro LEFT JOIN FETCH fro.department "
                        + "LEFT JOIN FETCH fro.address LEFT JOIN FETCH t.newEmployee to LEFT JOIN FETCH to.department "
                        + "LEFT JOIN FETCH to.address LEFT JOIN FETCH t.asset  ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE to.workId = :workId  AND ast.serialNumber = :serialNumber "
                        + "AND (:status IS NULL OR t.status = :status)"),
        @NamedQuery(
                name = "Transfer.details",
                query = "FROM Transfer t LEFT JOIN FETCH t.employee fro LEFT JOIN FETCH fro.department "
                        + "LEFT JOIN FETCH fro.address LEFT JOIN FETCH t.newEmployee to LEFT JOIN FETCH to.department "
                        + "LEFT JOIN FETCH to.address LEFT JOIN FETCH t.asset  ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE t.id = :transferId")
})
@Schema(description = "Transfer representation")
public class Transfer extends PanacheEntity {

    @CreationTimestamp
    @Column(name = "transfer_date", nullable = false)
    public LocalDate transferDate;

    @Column(name = "transfer_remarks", length = 4000)
    public String transferRemark;

    //    @ElementCollection
    @Enumerated(EnumType.STRING)
//    @ColumnDefault(value = "'TRANSFERED'")
//    @JoinColumn(
//            name = "transfer_status",
//            nullable = false,
//            foreignKey = @ForeignKey(
//                    name = "transfer_status_fk_constraint",
//                    foreignKeyDefinition = ""))
//    public Set<AllocationStatus> status = new HashSet<>();

    public AllocationStatus status;

    @NotNull
    @JoinColumn(
            name = "from_employee_fk",
            foreignKey = @ForeignKey(
                    name = "transfer_from_employee_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee employee;

    @NotNull
    @JoinColumn(
            name = "to_employee_fk",
            foreignKey = @ForeignKey(
                    name = "transfer_to_employee_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee newEmployee;

    @NotNull
    @JoinColumn(
            name = "asset_fk",
            foreignKey = @ForeignKey(
                    name = "asset_transfer_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Asset asset;

    @Column(name = "allocated_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String transferedBy;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    public String updatedBy;

    public static PanacheQuery<Transfer> listAll(AllocationStatus filteredStatus, String workId) {
        return find("SELECT t.asset, t.employee, t.status FROM Transfer t WHERE t.newEmployee.workId = :workId AND (:status IS NULL OR t.status = :status)",
                Parameters.with("workId", workId).and("status", filteredStatus));
    }

    public static Transfer preview(String workId, String serialNumber) {
        return find("#Transfer.preview",
                Parameters.with("workId", workId).and("serialNumber", serialNumber)
                        .and("status", AllocationStatus.ALLOCATED)
        ).firstResult();
    }

    public static Transfer qrDetails(Long transferId) {
        return find("#Transfer.details", Parameters.with("transferId", transferId))
                .firstResult();
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id='" + id + '\'' +
                ", transferDate=" + transferDate +
                ", transferRemark='" + transferRemark + '\'' +
                ", status=" + status +
                ", employee=" + employee +
                ", newEmployee=" + newEmployee +
                ", asset=" + asset +
                '}';
    }
}
