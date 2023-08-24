package com.japhet_sebastian.supplier;

import com.assets.management.assets.model.valueobject.SupplierType;
import com.japhet_sebastian.employee.BaseEntity;
import com.japhet_sebastian.organization.entity.AddressEntity;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
        name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uniqueEmailandPhone",
                        columnNames = {"company_email", "company_phone"
                        }
                )
        }
)
@NamedQueries({
        @NamedQuery(
                name = "SupplierEntity.getEmailOrPhone",
                query = "FROM SupplierEntity WHERE email = :email OR phone = :phone")
})
@Schema(description = "Supplier representation")
public class SupplierEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "supplier_uuid")
    public UUID supplier_uuid;

    @NotNull
    @Schema(required = true)
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    @Column(name = "company_name", length = 64, nullable = false)
    public String name;

    @NotNull
    @Schema(required = true)
    @Email
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "one or more character in not valid for proper email")
    @Column(name = "company_email", nullable = false)
    public String email;

    @NotNull
    @Schema(required = true)
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "must be in any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
                    + "+(255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510 and length btn 10 to 18 characters including space")
    @Column(name = "company_phone", length = 18, nullable = false)
    public String phone;

    // @NotNull
    @Column(name = "company_website")
    public String website;

    @NotNull
    @Schema(required = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type", nullable = false)
    public SupplierType supplierType;

    @NotNull
    @Schema(required = true)
    @Column(length = 500, nullable = false)
    @Pattern(
            regexp = "^[\\p{L} .'-?!;,]+$",
            message = "should include only letters, ' , ?, !, ; and - special characters")
    public String description;

    @Schema(required = true)
    @OneToOne(
            mappedBy = "supplier",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    public AddressEntity address;

//    public static Optional<SupplierEntity> findByEmailAndPhone(String email, String phone) {
//        return find("#Supplier.getEmailOrPhone", Parameters.with("email", email).and("phone", phone).map())
//                .firstResultOptional();
//    }
}
