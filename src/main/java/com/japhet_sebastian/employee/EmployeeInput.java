package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.EmploymentStatus;
import com.japhet_sebastian.vo.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class EmployeeInput {

    private String employeeId;

    @NotEmpty
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    private String firstName;

    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    private String middleName;

    @NotEmpty
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    private String lastName;

    private Gender gender;

    @NotEmpty
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
                    + "+(255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510 and length btn 10 to 18 characters including space")
    private String mobile;

    @NotEmpty
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "one or more character in not valid for proper email")
    private String email;

    @NotEmpty
    private String workId;

    @NotEmpty
    private LocalDate dateOfBirth;

    @NotEmpty
    private LocalDate hireDate;

    @NotEmpty
    private Set<EmploymentStatus> status;

    private String department_uuid;

    private String registeredBy;

    private String updatedBy;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

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

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Set<EmploymentStatus> getStatus() {
        return status;
    }

    public void setStatus(Set<EmploymentStatus> status) {
        this.status = status;
    }

    public String getDepartment_uuid() {
        return department_uuid;
    }

    public void setDepartment_uuid(String department_uuid) {
        this.department_uuid = department_uuid;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeInput that)) return false;
        return Objects.equals(getEmployeeId(), that.getEmployeeId()) && Objects.equals(getFirstName(), that.getFirstName()) && Objects.equals(getMiddleName(), that.getMiddleName()) && Objects.equals(getLastName(), that.getLastName()) && getGender() == that.getGender() && Objects.equals(getMobile(), that.getMobile()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getWorkId(), that.getWorkId()) && Objects.equals(getDateOfBirth(), that.getDateOfBirth()) && Objects.equals(getHireDate(), that.getHireDate()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getDepartment_uuid(), that.getDepartment_uuid()) && Objects.equals(getRegisteredBy(), that.getRegisteredBy()) && Objects.equals(getUpdatedBy(), that.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId(), getFirstName(), getMiddleName(), getLastName(), getGender(), getMobile(), getEmail(), getWorkId(), getDateOfBirth(), getHireDate(), getStatus(), getDepartment_uuid(), getRegisteredBy(), getUpdatedBy());
    }

    @Override
    public String toString() {
        return "EmployeeInput{" +
                "employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", workId='" + workId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", hireDate=" + hireDate +
                ", status=" + status +
                ", department_uuid='" + department_uuid + '\'' +
                ", registeredBy='" + registeredBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
