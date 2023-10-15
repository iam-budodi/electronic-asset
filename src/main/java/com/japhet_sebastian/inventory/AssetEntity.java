package com.japhet_sebastian.inventory;

import com.japhet_sebastian.procurement.purchase.PurchaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "Asset")
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(name = "unique_serial_no", columnNames = {"serial_number"})
})
@NamedQueries({
        @NamedQuery(name = "Asset.getSN", query = "FROM Asset WHERE serialNumber = :serialNumber")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ASSET")
@Schema(description = "Asset representation")
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "asset_uuid", nullable = false)
    private UUID assetId;

    @NotEmpty(message = "{Asset.brand.required}")
    @Schema(required = true)
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "brand_name", length = 32, nullable = false)
    private String brand;

    @NotEmpty(message = "{Asset.model.required}")
    @Schema(required = true)
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "{Alphanumeric.character}")
    @Column(name = "model_name", length = 32, nullable = false)
    private String model;

    @NotEmpty(message = "{Asset.model-number.required}")
    @Schema(required = true)
    @Column(name = "model_number", nullable = false)
    private String modelNumber;

    @NotEmpty(message = "{Asset.serial-number.required}")
    @Schema(required = true)
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @NotEmpty(message = "{Asset.manufacturer.required}")
    @Schema(required = true)
    @Column(nullable = false)
    private String manufacturer;

    @ToString.Exclude
    @JoinColumn(name = "category", foreignKey = @ForeignKey(name = "asset_category_constraint"))
    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryEntity category;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_string", foreignKey = @ForeignKey(name = "asset_label_constraint"))
    private QRCodeEntity label;

    @ToString.Exclude
    @NotNull
    @Schema(required = true)
    @JoinColumn(name = "purchase", nullable = false, foreignKey = @ForeignKey(name = "asset_purchase_constraint"))
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PurchaseEntity purchase;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate addedAt;

    @Column(name = "created_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    private String addedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    private String updatedBy;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AssetEntity that = (AssetEntity) o;
        return getAssetId() != null && Objects.equals(getAssetId(), that.getAssetId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}