package com.japhet_sebastian.procurement.supplier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.japhet_sebastian.organization.entity.AddressDto;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link SupplierEntity}
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierDto implements Serializable {

    String supplierId;

    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-?!;,]+$")
    @NotEmpty(message = "{Supplier.company-name.required}")
    String companyName;

    @Pattern(message = "{Email.invalid}", regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$")
    @Email
    @NotEmpty(message = "{Email.required}")
    String companyEmail;

    @Pattern(message = "{Phone.number.invalid}", regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$")
    @NotEmpty(message = "{Phone.number.required}")
    String companyPhone;

    @NotNull(message = "{Supplier.category.required}")
    SupplierType supplierType;

    String website;

    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-?!;,]+$")
    String description;

    //    @NotNull(message = "{Address.field.required}")
    AddressDto address;

    String supplierAddress;

    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-]+$")
    String registeredBy;

    String registeredAt;

    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-]+$")
    String updatedBy;

    String updatedAt;

}