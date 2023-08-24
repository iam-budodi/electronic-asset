package com.japhet_sebastian.employee;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.vo.EmploymentStatus;
import com.japhet_sebastian.vo.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class Employee {

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
    private Set<EmploymentStatus> status;

    private String departmentName;

    private AddressEntity address;

    private Integer age;

    private Integer timeOfService;

    private LocalDate retireAt;
    private LocalDate registeredAt;

    private LocalDate updatedAt;

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

    public Set<EmploymentStatus> getStatus() {
        return status;
    }

    public void setStatus(Set<EmploymentStatus> status) {
        this.status = status;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getTimeOfService() {
        return timeOfService;
    }

    public void setTimeOfService(Integer timeOfService) {
        this.timeOfService = timeOfService;
    }

    public LocalDate getRetireAt() {
        return retireAt;
    }

    public void setRetireAt(LocalDate retireAt) {
        this.retireAt = retireAt;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
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
        return Objects.equals(getEmployeeId(), employee.getEmployeeId()) && Objects.equals(getFirstName(), employee.getFirstName()) && Objects.equals(getMiddleName(), employee.getMiddleName()) && Objects.equals(getLastName(), employee.getLastName()) && getGender() == employee.getGender() && Objects.equals(getMobile(), employee.getMobile()) && Objects.equals(getEmail(), employee.getEmail()) && Objects.equals(getStatus(), employee.getStatus()) && Objects.equals(getDepartmentName(), employee.getDepartmentName()) && Objects.equals(getAddress(), employee.getAddress()) && Objects.equals(getAge(), employee.getAge()) && Objects.equals(getTimeOfService(), employee.getTimeOfService()) && Objects.equals(getRetireAt(), employee.getRetireAt()) && Objects.equals(getRegisteredAt(), employee.getRegisteredAt()) && Objects.equals(getUpdatedAt(), employee.getUpdatedAt()) && Objects.equals(getRegisteredBy(), employee.getRegisteredBy()) && Objects.equals(getUpdatedBy(), employee.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId(), getFirstName(), getMiddleName(), getLastName(), getGender(), getMobile(), getEmail(), getStatus(), getDepartmentName(), getAddress(), getAge(), getTimeOfService(), getRetireAt(), getRegisteredAt(), getUpdatedAt(), getRegisteredBy(), getUpdatedBy());
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", departmentName='" + departmentName + '\'' +
                ", address=" + address +
                ", age=" + age +
                ", timeOfService=" + timeOfService +
                ", retireAt=" + retireAt +
                ", registeredAt=" + registeredAt +
                ", updatedAt=" + updatedAt +
                ", registeredBy='" + registeredBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
