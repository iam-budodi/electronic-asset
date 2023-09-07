package com.japhet_sebastian.supplier;

import com.japhet_sebastian.employee.entity.BaseEntity;
import com.japhet_sebastian.organization.entity.AddressEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

@Entity(name = "Supplier")
@Table(name = "suppliers", uniqueConstraints = {@UniqueConstraint(name = "uniqueEmailAndPhone",
        columnNames = {"company_email", "company_phone"})})
@NamedQueries({@NamedQuery(name = "SupplierEntity.getEmailOrPhone",
        query = "FROM Supplier WHERE companyEmail = :email OR companyPhone = :phone")})
@Schema(description = "Supplier representation")
public class SupplierEntity extends BaseEntity {

    @Id
    public UUID supplierId;

    @Schema(required = true)
    @NotEmpty(message = "{Supplier.company-name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "company_name", length = 64, nullable = false)
    public String companyName;

    @Schema(required = true)
    @Email
    @NotEmpty(message = "{Email.required}")
    @Pattern(regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "{Email.invalid}")
    @Column(name = "company_email", nullable = false)
    public String companyEmail;

    @Schema(required = true)
    @NotEmpty(message = "{Phone.number.required}")
    @Pattern(regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "{Phone.number.invalid}")
    @Column(name = "company_phone", length = 18, nullable = false)
    public String companyPhone;

    @Column(name = "company_website")
    public String website;

    @Schema(required = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type", nullable = false)
    public SupplierType supplierType;

    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(length = 500, nullable = false)
    public String description;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "supplier_uuid", foreignKey = @ForeignKey(name = "supplier_address_fk_constraint"))
    public AddressEntity address;

}
