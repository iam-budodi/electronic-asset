package com.japhet_sebastian.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.EmploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name = "Employee")
@Table(name = "employees", uniqueConstraints = {@UniqueConstraint(name = "unique_email_phone",
        columnNames = {"email_address", "phone_number"}),
        @UniqueConstraint(name = "unique_work_id", columnNames = {"work_id"})})
@NamedQueries({@NamedQuery(name = "Employee.getByEmailOrPhone",
        query = "FROM Employee WHERE email = :email OR mobile = :mobile")})
@Schema(description = "Employee representation")
public class EmployeeEntity extends Person {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "employee_uuid", foreignKey = @ForeignKey(name = "employee_address_fk_constraint"))
    public AddressEntity address;

    @Id
    private UUID employeeId;

    @Schema(required = true)
    @NotEmpty(message = "{Employee.work-id.required}")
    @Column(name = "work_id")
    private String workId;

    @Schema(required = true)
    @NotNull(message = "{Employee.dob.required}")
    @Column(name = "birthdate")
    private LocalDate dateOfBirth;

    @Schema(required = true)
    @NotNull(message = "{Employee.hire-date.required}")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @NotEmpty(message = "{Employee.status.required}")
    @Schema(required = true)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "employment_status", joinColumns = @JoinColumn(name = "employee_uuid"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "employment_status", nullable = false)
    private Set<EmploymentStatus> status;

    //    @NotEmpty(message = "{Department.field.required}")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "department_uuid", foreignKey = @ForeignKey(name = "employee_department_fk_constraint", foreignKeyDefinition = ""))
    private DepartmentEntity department;

    @Transient
    private Integer age;

    @Transient
    private Integer timeOfService;

    @Transient
    private LocalDate retireAt;

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void calculateAgeAndRetireDate() {
        if (hireDate == null) {
            retireAt = null;
            return;
        } else if (dateOfBirth == null) {
            age = null;
            return;
        }

        timeOfService = Period.between(hireDate, LocalDate.now()).getYears();
        age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        retireAt = LocalDate.now().plusYears(60 - (LocalDate.now().getYear() - dateOfBirth.getYear()));
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
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

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getEmployeeId(), that.getEmployeeId()) && Objects.equals(getWorkId(), that.getWorkId()) && Objects.equals(getDateOfBirth(), that.getDateOfBirth()) && Objects.equals(getHireDate(), that.getHireDate()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getDepartment(), that.getDepartment()) && Objects.equals(getAge(), that.getAge()) && Objects.equals(getTimeOfService(), that.getTimeOfService()) && Objects.equals(getRetireAt(), that.getRetireAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAddress(), getEmployeeId(), getWorkId(), getDateOfBirth(), getHireDate(), getStatus(), getDepartment(), getAge(), getTimeOfService(), getRetireAt());
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" +
                "address=" + address +
                ", employeeId=" + employeeId +
                ", workId='" + workId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", hireDate=" + hireDate +
                ", status=" + status +
                ", department=" + department +
                ", age=" + age +
                ", timeOfService=" + timeOfService +
                ", retireAt=" + retireAt +
                '}';
    }
}

