package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "asset_transfers")
@NamedQueries({
        @NamedQuery(
                name = "Transfer.preview",
                query = "FROM Transfer t LEFT JOIN FETCH t.prevCustodian fro LEFT JOIN FETCH fro.department "
                        + "LEFT JOIN FETCH fro.address LEFT JOIN FETCH t.currentCustodian to LEFT JOIN FETCH to.department "
                        + "LEFT JOIN FETCH to.address LEFT JOIN FETCH t.asset  ast LEFT JOIN FETCH ast.category "
                        + "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
                        + "LEFT JOIN FETCH s.address WHERE to.id = :employeeId  AND ast.serialNumber = :serialNumber "
                        + "AND (:status IS NULL OR :status MEMBER OF t.status)"),
        @NamedQuery(
                name = "Transfer.details",
                query = "FROM Transfer t LEFT JOIN FETCH t.prevCustodian fro LEFT JOIN FETCH fro.department "
                        + "LEFT JOIN FETCH fro.address LEFT JOIN FETCH t.currentCustodian to LEFT JOIN FETCH to.department "
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

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'TRANSFERED'")
    @JoinColumn(
            name = "transfer_status",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "transfer_status_fk_constraint",
                    foreignKeyDefinition = ""))
    public Set<AllocationStatus> status = new HashSet<>();

    @NotNull
    @JoinColumn(
            name = "from_employee_fk",
            foreignKey = @ForeignKey(
                    name = "transfer_from_employee_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee prevCustodian;

    @NotNull
    @JoinColumn(
            name = "to_employee_fk",
            foreignKey = @ForeignKey(
                    name = "transfer_to_employee_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    public Employee currentCustodian;

    @NotNull
    @JoinColumn(
            name = "asset_fk",
            foreignKey = @ForeignKey(
                    name = "asset_transfer_fk_constraint",
                    foreignKeyDefinition = ""))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Asset asset;

    public static PanacheQuery<Transfer> listAll(AllocationStatus filteredStatus, Long employeeId) {
        return find("FROM Transfer t WHERE t.currentCustodian.id = :employeeId AND (:status IS NULL OR :status MEMBER OF t.status)",
                Parameters.with("employeeId", employeeId).and("status", filteredStatus));
    }

    public static Transfer preview(Long employeeId, String serialNumber) {
        return find("#Transfer.preview",
                Parameters.with("employeeId", employeeId).and("serialNumber", serialNumber)
                        .and("status", AllocationStatus.ALLOCATED)
        ).firstResult();
    }

    public static Transfer qrDetails(Long transferId) {
        return find("#Transfer.details", Parameters.with("transferId", transferId))
                .firstResult();
    }

}
