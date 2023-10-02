package com.japhet_sebastian.procurement.supplier;

import com.japhet_sebastian.employee.BaseEntity;
import com.japhet_sebastian.organization.entity.AddressEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "Supplier")
@Table(name = "suppliers", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueEmailAndPhone", columnNames = {"company_email", "company_phone"})
})
@NamedQueries({@NamedQuery(name = "SupplierEntity.getEmailOrPhone",
        query = "FROM Supplier WHERE companyEmail = :email OR companyPhone = :phone")})
@Schema(description = "Supplier representation")
public class SupplierEntity extends BaseEntity {

    @Id
    @Column(nullable = false)
    private UUID supplierId;

    @Schema(required = true)
    @NotEmpty(message = "{Supplier.company-name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "company_name", length = 64, nullable = false)
    private String companyName;

    @Schema(required = true)
    @Email
    @NotEmpty(message = "{Email.required}")
    @Pattern(regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "{Email.invalid}")
    @Column(name = "company_email", nullable = false)
    private String companyEmail;

    @Schema(required = true)
    @NotEmpty(message = "{Phone.number.required}")
    @Pattern(regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "{Phone.number.invalid}")
    @Column(name = "company_phone", length = 18, nullable = false)
    private String companyPhone;

    @Column(name = "company_website")
    private String website;

    @Schema(required = true)
    @NotNull(message = "{Supplier.category.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type", nullable = false)
    private SupplierType supplierType;

    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(length = 500, nullable = true)
    private String description;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "supplier_uuid", foreignKey = @ForeignKey(name = "supplier_address_fk_constraint"))
    private AddressEntity address;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SupplierEntity that = (SupplierEntity) o;
        return getSupplierId() != null && Objects.equals(getSupplierId(), that.getSupplierId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
