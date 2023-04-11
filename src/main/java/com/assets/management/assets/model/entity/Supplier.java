package com.assets.management.assets.model.entity;

import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.assets.management.assets.model.valueobject.SupplierType;

import io.quarkus.panache.common.Parameters;

@Entity
@Table(
        name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uniqueEmailandPhone",
                        columnNames = {"company_email", "company_phone"})
        }
)
@NamedQueries({
        @NamedQuery(
                name = "Supplier.getEmailOrPhone",
                query = "FROM Supplier WHERE email = :email OR phone = :phone")
}
)
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

    // ^(((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3}$
    @NotNull
    @Schema(required = true)
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
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
//
//	@JsonIgnore	
//	@OneToOne(
//			mappedBy = "supplier", 
//			orphanRemoval = true,
//			cascade = CascadeType.ALL, 
//			fetch = FetchType.LAZY)
//	public Purchase purchase;

    public static Optional<Supplier> findByEmailAndPhone(String email, String phone) {
        return find(
                "#Supplier.getEmailOrPhone",
                Parameters.with("email", email).and("phone", phone).map())
                .firstResultOptional();
    }
}
