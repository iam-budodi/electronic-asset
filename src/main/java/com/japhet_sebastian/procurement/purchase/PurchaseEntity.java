package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.procurement.supplier.SupplierEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "Purchase")
@Table(name = "purchases", uniqueConstraints = {@UniqueConstraint(
        name = "unique_invoice_number", columnNames = {"purchase_invoice_number"})
})
@Schema(description = "Purchase representation")
public class PurchaseEntity {

    @Transient
    public BigDecimal totalPurchaseCost;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "purchase_uuid", nullable = false)
    private UUID purchaseId;

    @NotNull(message = "{Purchase.date.required}")
    @PastOrPresent(message = "{Purchase.future-date.required}")
    @Schema(required = true)
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @NotNull(message = "{Purchase.quantity.required}")
    @PositiveOrZero(message = "{Purchase.quantity-number.required}")
    @Schema(required = true)
    @Column(name = "purchase_quantity", nullable = false)
    private Integer purchaseQty;

    @Min(value = 1, message = "{Purchase.min-price.required}")
    @NotNull(message = "{Purchase.price.required}")
    @Schema(required = true)
    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    @NotEmpty(message = "Purchase.invoice-number.required")
    @Schema(required = true)
    @Column(name = "purchase_invoice_number", nullable = false)
    private String invoiceNumber;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_uuid", foreignKey = @ForeignKey(name = "purchase_supplier_fk_constraint"))
    private SupplierEntity supplier;

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void totalPurchaseCost() {
        if (Objects.isNull(purchaseQty) || Objects.isNull(purchasePrice)) {
            totalPurchaseCost = BigDecimal.ZERO;
            return;
        }
        totalPurchaseCost = purchasePrice.multiply(BigDecimal.valueOf(purchaseQty));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PurchaseEntity that = (PurchaseEntity) o;
        return getPurchaseId() != null && Objects.equals(getPurchaseId(), that.getPurchaseId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}