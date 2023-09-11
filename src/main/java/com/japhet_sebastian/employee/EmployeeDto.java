package com.japhet_sebastian.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.japhet_sebastian.organization.entity.AddressDto;
import com.japhet_sebastian.organization.entity.DepartmentDto;
import com.japhet_sebastian.vo.EmploymentStatus;
import com.japhet_sebastian.vo.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link EmployeeEntity}
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDto implements Serializable {

    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-?!;,]+$")
    @NotEmpty(message = "{Employee.firstName.required}")
    String firstName;

    @Size(message = "{Sixty-four.string.length}", min = 1, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-?!;,]+$")
    String middleName;

    @Size(message = "{Sixty-four.string.length}", min = 2, max = 64)
    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-?!;,]+$")
    @NotEmpty(message = "{Employee.lastName.required}")
    String lastName;

    String fullName;

    Gender gender;

    @Pattern(message = "{Phone.number.invalid}", regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$")
    @NotEmpty(message = "{Phone.number.required}")
    String mobile;

    @Pattern(message = "{Email.invalid}", regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$")
    @Email
    @NotEmpty(message = "{Email.required}")
    String email;

    String employeeId;

    @NotEmpty(message = "{Employee.work-id.required}")
    String workId;

    DepartmentDto department;

    String departmentName;

    @NotNull(message = "{Address.field.required}")
    AddressDto address;

    String employeeAddress;

    @NotNull(message = "{Employee.dob.required}")
    String dateOfBirth;

    @NotNull(message = "{Employee.hire-date.required}")
    String hireDate;

    String timeOfService;

    Set<EmploymentStatus> status;

    String registeredAt;

    String updatedAt;

    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-]+$")
    String registeredBy;

    @Pattern(message = "{String.special.character}", regexp = "^[\\p{L} .'-]+$")
    String updatedBy;
}