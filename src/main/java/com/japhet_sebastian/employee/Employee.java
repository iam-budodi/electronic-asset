package com.japhet_sebastian.employee;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

public class Employee extends EmployeeBase {

    private String workId;

    @NotNull(message = "{Employee.dob.required}")
    private LocalDate dateOfBirth;

    @NotNull(message = "{Employee.hire-date.required}")
    private LocalDate hireDate;

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
        return Objects.equals(getWorkId(), employee.getWorkId()) && Objects.equals(getDateOfBirth(), employee.getDateOfBirth()) && Objects.equals(getHireDate(), employee.getHireDate()) && Objects.equals(getStreet(), employee.getStreet()) && Objects.equals(getDistrict(), employee.getDistrict()) && Objects.equals(getCity(), employee.getCity()) && Objects.equals(getPostalCode(), employee.getPostalCode()) && Objects.equals(getCountry(), employee.getCountry()) && Objects.equals(getRegisteredBy(), employee.getRegisteredBy()) && Objects.equals(getUpdatedBy(), employee.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWorkId(), getDateOfBirth(), getHireDate(), getStreet(), getDistrict(), getCity(), getPostalCode(), getCountry(), getRegisteredBy(), getUpdatedBy());
    }

    @Override
    public String toString() {
        return "Employee{" +
                "workId='" + workId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", hireDate=" + hireDate +
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
