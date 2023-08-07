package com.japhet_sebastian.employee;

import com.assets.management.assets.model.valueobject.EmploymentStatus;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_email_phone",
                        columnNames = {"email_address", "phone_number"}),
                @UniqueConstraint(name = "unique_work_id", columnNames = {"work_id"})})
@NamedQueries({
        @NamedQuery(name = "EmployeeEntity.getEmailOrPhone",
                query = "FROM EmployeeEntity WHERE email = :email OR mobile = :mobile")
})
@Schema(description = "Employee representation")
public class EmployeeEntity extends Person {

    @NotNull
    @Schema(required = true)
    @Column(name = "work_id")
    public String workId;

    @NotNull
    @Schema(required = true)
    @Column(name = "birthdate")
    public LocalDate dateOfBirth;

    @NotNull
    @Schema(required = true)
    @Column(name = "hire_date", nullable = false)
    public LocalDate hireDate;

    @NotNull
    @Schema(required = true)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    public Set<EmploymentStatus> status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_fk", foreignKey = @ForeignKey(name = "employee_department_fk_constraint", foreignKeyDefinition = ""))
    public DepartmentEntity department;

    @OneToOne(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public AddressEntity address;

    @Transient
    public Integer age;

    @Transient
    public Integer timeOfService;

    @Transient
    public LocalDate retireAt;

    public static boolean checkByEmailAndPhone(String email, String mobile) {
        return find("#Employee.getEmailOrPhone", Parameters.with("email", email).and("mobile", mobile).map()).firstResultOptional().isPresent();
    }

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
}

