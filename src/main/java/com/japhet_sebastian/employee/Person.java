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

import java.util.Objects;

@MappedSuperclass
public class Person extends BaseEntity {

    @NotEmpty
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    @Column(name = "middle_name", length = 64)
    private String middleName;

    @NotEmpty
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$"
    @NotEmpty
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
                    + "+(255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510 and length btn 10 to 18 characters including space")
    @Column(name = "phone_number", length = 18, nullable = false)
    private String mobile;

    @NotEmpty
    @Email
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "one or more character in not valid for proper email")
    @Column(name = "email_address", nullable = false)
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getFirstName(), person.getFirstName()) && Objects.equals(getMiddleName(), person.getMiddleName()) && Objects.equals(getLastName(), person.getLastName()) && getGender() == person.getGender() && Objects.equals(getMobile(), person.getMobile()) && Objects.equals(getEmail(), person.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFirstName(), getMiddleName(), getLastName(), getGender(), getMobile(), getEmail());
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
