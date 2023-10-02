package com.japhet_sebastian.organization.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "Department")
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "unique_department_name", columnNames = {"department_name"})
})
@NamedQueries({@NamedQuery(name = "Department.getName",
        query = "FROM Department d LEFT JOIN FETCH d.college WHERE LOWER(d.departmentName) = :name")})
@Schema(description = "Department representation")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "department_uuid", nullable = false)
    private UUID departmentId;

    @Schema(required = true)
    @NotEmpty(message = "{Department.field.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "department_name", length = 64, nullable = false)
    private String departmentName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    @Column(name = "department_code", length = 10)
    private String departmentCode;

    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(length = 64)
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "college_uuid", nullable = false, foreignKey = @ForeignKey(name = "college_department_fk_constraint"))
    private CollegeEntity college;

//
//    @OneToOne(mappedBy = "department")
//    public HeadOfDepartment headOfDepartment;
//
//    public static List<DepartmentEntity> findAllOrderByName() {
//        return listAll(Sort.by("name"));
//    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DepartmentEntity that = (DepartmentEntity) o;
        return getDepartmentId() != null && Objects.equals(getDepartmentId(), that.getDepartmentId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
