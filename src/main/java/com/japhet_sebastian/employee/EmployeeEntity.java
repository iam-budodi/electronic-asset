package com.japhet_sebastian.employee;

import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.EmploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity(name = "Employee")
@Table(name = "employees", uniqueConstraints = {@UniqueConstraint(name = "unique_email_phone",
        columnNames = {"email_address", "phone_number"}),
        @UniqueConstraint(name = "unique_work_id", columnNames = {"work_id"})})
@NamedQueries({@NamedQuery(name = "Employee.getEmailOrPhone",
        query = "FROM Employee WHERE email = :email OR mobile = :mobile")})
@Schema(description = "Employee representation")
public class EmployeeEntity extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_uuid")
    private UUID employeeId;

    @NotEmpty
    @Schema(required = true)
    @Column(name = "work_id")
    private String workId;

    @NotEmpty
    @Schema(required = true)
    @Column(name = "birthdate")
    private LocalDate dateOfBirth;

    @NotEmpty
    @Schema(required = true)
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @NotEmpty
    @Schema(required = true)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private Set<EmploymentStatus> status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
        return Objects.equals(getEmployeeId(), that.getEmployeeId()) && Objects.equals(getWorkId(), that.getWorkId()) && Objects.equals(getDateOfBirth(), that.getDateOfBirth()) && Objects.equals(getHireDate(), that.getHireDate()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getDepartment(), that.getDepartment()) && Objects.equals(getAge(), that.getAge()) && Objects.equals(getTimeOfService(), that.getTimeOfService()) && Objects.equals(getRetireAt(), that.getRetireAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmployeeId(), getWorkId(), getDateOfBirth(), getHireDate(), getStatus(), getDepartment(), getAge(), getTimeOfService(), getRetireAt());
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" +
                "employeeId=" + employeeId +
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

