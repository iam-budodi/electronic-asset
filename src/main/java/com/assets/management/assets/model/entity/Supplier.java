package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.SupplierType;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

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
                name = "Supplier.getEmailOrPhone",
                query = "FROM Supplier WHERE email = :email OR phone = :phone")
})
@Schema(description = "Supplier representation")
public class Supplier extends BaseEntity {

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
    public Address address;

    public static Optional<Supplier> findByEmailAndPhone(String email, String phone) {
        return find("#Supplier.getEmailOrPhone", Parameters.with("email", email).and("phone", phone).map())
                .firstResultOptional();
    }
}
