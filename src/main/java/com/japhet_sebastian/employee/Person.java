package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
public class Person extends BaseEntity {

    @NotEmpty(message = "{Employee.firstName.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Size(min = 1, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "middle_name", length = 64)
    private String middleName;

    @NotEmpty(message = "{Employee.lastName.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-?!;,]+$", message = "{String.special.character}")
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotEmpty(message = "{Phone.number.required}")
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "{Phone.number.invalid}")
    @Column(name = "phone_number", length = 18, nullable = false)
    private String mobile;

    @Schema(required = true)
    @Email
    @NotEmpty(message = "{Email.required}")
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "{Email.invalid}")
    @Column(name = "email_address", nullable = false)
    private String email;

}
