package com.japhet_sebastian.employee;

import com.assets.management.assets.model.entity.BaseEntity;
import com.assets.management.assets.model.valueobject.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@MappedSuperclass
public class Person extends BaseEntity {

    @NotNull
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    @Column(name = "first_name", length = 64, nullable = false)
    public String firstName;

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    @Column(name = "middle_name", length = 64)
    public String middleName;

    @NotNull
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    @Column(name = "last_name", length = 64, nullable = false)
    public String lastName;

    @Enumerated(EnumType.STRING)
    public Gender gender;

    // "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$"
    @NotNull
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
                    + "+(255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510 and length btn 10 to 18 characters including space")
    @Column(name = "phone_number", length = 18, nullable = false)
    public String mobile;

    @NotNull
    @Email
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "one or more character in not valid for proper email")
    @Column(name = "email_address", nullable = false)
    public String email;
}
