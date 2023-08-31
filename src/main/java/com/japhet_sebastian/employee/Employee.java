package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.EmploymentStatus;
import com.japhet_sebastian.vo.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class Employee extends EmployeeBase {

    private String employeeId;

    @NotEmpty(message = "{Employee.firstName.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String firstName;

    @Size(min = 1, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String middleName;

    @NotEmpty(message = "{Employee.lastName.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String lastName;

    @NotNull(message = "{Employee.dob.required}")
    private LocalDate dateOfBirth;

    private Gender gender;

    @NotEmpty(message = "{Phone.number.required}")
    @Pattern(
            regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$",
            message = "{Phone.number.invalid}")
    private String mobile;

    @NotEmpty(message = "{Email.required}")
    @Pattern(
            regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$",
            message = "{Email.invalid}")
    private String email;

    private String workId;

    @NotNull(message = "{Employee.hire-date.required}")
    private LocalDate hireDate;

    @NotEmpty(message = "{Employee.status.required}")
    private Set<EmploymentStatus> status;

    private String timeOfService;

    @NotEmpty(message = "{Department.field.required}")
    private String departmentName;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String street;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String district;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String city;

    @Size(min = 5, max = 5, message = "{Postal.code.length}")
    @Pattern(regexp = "^\\d{5}$", message = "{Postal.code.length}")
    private String postalCode;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String country;

    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "{String.special.character}")
    private String registeredBy;


    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "{String.special.character}")
    private String updatedBy;

    @Override
    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public Set<EmploymentStatus> getStatus() {
        return status;
    }

    @Override
    public void setStatus(Set<EmploymentStatus> status) {
        this.status = status;
    }

    public String getTimeOfService() {
        return timeOfService;
    }

    public void setTimeOfService(String timeOfService) {
        this.timeOfService = timeOfService;
    }

    @Override
    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
        if (!(o instanceof Employee employee)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getEmployeeId(), employee.getEmployeeId()) && Objects.equals(getFirstName(), employee.getFirstName()) && Objects.equals(getMiddleName(), employee.getMiddleName()) && Objects.equals(getLastName(), employee.getLastName()) && Objects.equals(getDateOfBirth(), employee.getDateOfBirth()) && getGender() == employee.getGender() && Objects.equals(getMobile(), employee.getMobile()) && Objects.equals(getEmail(), employee.getEmail()) && Objects.equals(getWorkId(), employee.getWorkId()) && Objects.equals(getHireDate(), employee.getHireDate()) && Objects.equals(getStatus(), employee.getStatus()) && Objects.equals(getTimeOfService(), employee.getTimeOfService()) && Objects.equals(getDepartmentName(), employee.getDepartmentName()) && Objects.equals(getStreet(), employee.getStreet()) && Objects.equals(getDistrict(), employee.getDistrict()) && Objects.equals(getCity(), employee.getCity()) && Objects.equals(getPostalCode(), employee.getPostalCode()) && Objects.equals(getCountry(), employee.getCountry()) && Objects.equals(getRegisteredBy(), employee.getRegisteredBy()) && Objects.equals(getUpdatedBy(), employee.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmployeeId(), getFirstName(), getMiddleName(), getLastName(), getDateOfBirth(), getGender(), getMobile(), getEmail(), getWorkId(), getHireDate(), getStatus(), getTimeOfService(), getDepartmentName(), getStreet(), getDistrict(), getCity(), getPostalCode(), getCountry(), getRegisteredBy(), getUpdatedBy());
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", workId='" + workId + '\'' +
                ", hireDate=" + hireDate +
                ", status=" + status +
                ", timeOfService='" + timeOfService + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", street='" + street + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", registeredBy='" + registeredBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
