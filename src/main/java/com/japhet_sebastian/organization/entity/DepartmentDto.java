package com.japhet_sebastian.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link DepartmentEntity}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentDto implements Serializable {

    String departmentId;

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

    CollegeDto college;
}