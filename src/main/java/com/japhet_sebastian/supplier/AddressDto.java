package com.japhet_sebastian.supplier;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.japhet_sebastian.organization.entity.AddressEntity}
 */
@Data
public class AddressDto implements Serializable {

    @Size(message = "{Thirty-two.string.length}", min = 2, max = 32)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{Address.field.required}")
    String street;

    @Size(message = "{Thirty-two.string.length}", min = 2, max = 32)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{Address.field.required}")
    String district;

    @Size(message = "{Thirty-two.string.length}", min = 2, max = 32)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{Address.field.required}")
    String city;

    @Size(message = "{Postal.code.length}", min = 5, max = 5)
    @Pattern(message = "{Postal.code.length}", regexp = "^\\d{5}$")
    String postalCode;

    @Size(message = "{Thirty-two.string.length}", min = 2, max = 32)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{Address.field.required}")
    String country;

//    String addressId;
}