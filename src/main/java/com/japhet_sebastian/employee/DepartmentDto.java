package com.japhet_sebastian.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.japhet_sebastian.organization.entity.DepartmentEntity}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentDto implements Serializable {
    UUID departmentId;
    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    @NotEmpty(message = "{Department.field.required}")
    String departmentName;
    @Size(message = "{Alphanumeric.character.length}", min = 2, max = 10)
    @Pattern(message = "{Alphanumeric.character}", regexp = "^[\\p{L}\\p{Nd} _]+$")
    String departmentCode;
    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-/]+$")
    String description;
}