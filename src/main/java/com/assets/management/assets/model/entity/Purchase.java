package com.assets.management.assets.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.assets.management.assets.model.valueobject.PurchasePerSupplier;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@Entity
@Table(
        name = "purchases",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_invoice_number",
                        columnNames = {"purchase_invoice_number"}
                )
        }
)
@Schema(description = "Purchase representation")
public class Purchase extends PanacheEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(required = true)
    @PastOrPresent
    @Column(name = "purchase_date", nullable = false)
    public LocalDate purchaseDate;

    @Min(1)
    @NotNull
    @Schema(required = true)
//	@Digits(fraction = 0, integer = 0)
    @Column(name = "purchase_quantity", nullable = false)
    public Integer purchaseQty;

    @Min(1)
    @NotNull
    @Schema(required = true)
    @Column(name = "purchase_price", nullable = false)
    public BigDecimal purchasePrice;

    @NotNull
    @Schema(required = true)
    @Column(name = "purchase_invoice_number", nullable = false)
    public String invoiceNumber;

    //	@MapsId
//	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "supplier_fk",
            foreignKey = @ForeignKey(
                    name = "purchase_supplier_fk_constraint",
                    foreignKeyDefinition = ""))
    public Supplier supplier;

    @Transient
    public BigDecimal totalPurchaseCost;

    public static Optional<Purchase> findByInvoice(String invoiceNumber) {
        return find("LOWER(invoiceNumber)", invoiceNumber.toLowerCase())
                .firstResultOptional();
    }

    public static List<PurchasePerSupplier> purchasePerSupplier() {
        return find("SELECT p.supplier.name AS supplier, COUNT(p.supplier) AS purchases "
                + "FROM Purchase p GROUP BY p.supplier.name").project(PurchasePerSupplier.class).list();
    }

    // method overloading
    public static PanacheQuery<Purchase> retrieveAllOrById() {
        return retrieveAllOrById(null);
    }

    public static PanacheQuery<Purchase> retrieveAllOrById(Long purchaseId) {
        return find("FROM Purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
                        + "WHERE (:purchaseId IS NULL OR p.id = :purchaseId) ",
                Sort.by("p.purchaseDate").and("p.purchaseQty", Sort.Direction.Descending),
                Parameters.with("purchaseId", purchaseId));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void calculateAgeAndRetireDate() {
        if (purchaseQty == null || purchasePrice == null) {
            totalPurchaseCost = BigDecimal.ZERO;
            return;
        }

        totalPurchaseCost = purchasePrice.multiply(BigDecimal.valueOf(purchaseQty));
    }
}
