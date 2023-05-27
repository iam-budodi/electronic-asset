package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.PurchasePerSupplier;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

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

    @Serial
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
                    name = "purchase_supplier_fk_constraint")
    )
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

    public static PanacheQuery<Purchase> getAll(String searchValue, LocalDate date, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sortVariable = String.format("p.%s", column);
        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT DISTINCT p FROM Purchase p LEFT JOIN p.supplier s LEFT JOIN s.address " +
                "WHERE (:searchValue IS NULL OR LOWER(p.invoiceNumber) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(p.supplier.name) LIKE :searchValue) ";

        if (date != null) queryString += "AND p.purchaseDate = :date";
        else queryString += "AND (:date IS NULL OR p.purchaseDate = :date)";

        return find(
                queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue).and("date", date)
        );
    }

    public static PanacheQuery<Purchase> getById(Long purchaseId) {

        return find("FROM Purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
                        + "WHERE p.id = :purchaseId ",
                Parameters.with("purchaseId", purchaseId));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void totalPurchaseCost() {
        if (purchaseQty == null || purchasePrice == null) {
            totalPurchaseCost = BigDecimal.ZERO;
            return;
        }

        totalPurchaseCost = purchasePrice.multiply(BigDecimal.valueOf(purchaseQty));
    }
}
