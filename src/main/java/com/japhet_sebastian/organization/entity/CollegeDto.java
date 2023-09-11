package com.japhet_sebastian.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link CollegeEntity}
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollegeDto implements Serializable {

    String collegeId;

    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{College.name.required}")
    String collegeName;

    @Size(message = "{Alphanumeric.character.length}", min = 2, max = 10)
    @Pattern(message = "{Alphanumeric.character}", regexp = "^[\\p{L}\\p{Nd} _]+$")
    String collegeCode;

    AddressDto address;
    String collegeAddress;


}