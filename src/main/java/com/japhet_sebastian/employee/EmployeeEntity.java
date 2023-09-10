package com.japhet_sebastian.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.EmploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
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
    @ToString.Exclude
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

    @Schema(required = true)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "employment_status", joinColumns = @JoinColumn(name = "employee_uuid"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "employment_status", nullable = false)
    private Set<EmploymentStatus> status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "department_uuid", foreignKey = @ForeignKey(name = "employee_department_fk_constraint", foreignKeyDefinition = ""))
    @ToString.Exclude
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EmployeeEntity that = (EmployeeEntity) o;
        return getEmployeeId() != null && Objects.equals(getEmployeeId(), that.getEmployeeId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

