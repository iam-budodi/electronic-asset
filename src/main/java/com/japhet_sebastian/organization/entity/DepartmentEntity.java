package com.japhet_sebastian.organization.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "Department")
@Table(name = "departments", uniqueConstraints = {@UniqueConstraint(name = "unique_department_name",
        columnNames = {"department_name"})})
@NamedQueries({@NamedQuery(name = "Department.getName", query = "FROM Department WHERE LOWER(departmentName) = :name")})
@Schema(description = "Department representation")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "department_uuid")
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

    @Schema(required = true)
    @NotEmpty(message = "{Department.field.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_fk", nullable = false, foreignKey = @ForeignKey(name = "college_department_fk_constraint"))
    private CollegeEntity college;

//
//    @OneToOne(mappedBy = "department")
//    public HeadOfDepartment headOfDepartment;
//
//    public static List<DepartmentEntity> findAllOrderByName() {
//        return listAll(Sort.by("name"));
//    }
//
//    public static Optional<DepartmentEntity> findByName(String name) {
//        return find(
//                "#Department.getName", Parameters.with("name", name.toLowerCase()))
//                .firstResultOptional();
//    }


    public DepartmentEntity() {
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CollegeEntity getCollege() {
        return college;
    }

    public void setCollege(CollegeEntity college) {
        this.college = college;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentEntity that)) return false;
        return Objects.equals(getDepartmentId(), that.getDepartmentId())
                && Objects.equals(getDepartmentName(), that.getDepartmentName())
                && Objects.equals(getDepartmentCode(), that.getDepartmentCode())
                && Objects.equals(getDescription(), that.getDescription())
                && Objects.equals(getCollege(), that.getCollege());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDepartmentId(), getDepartmentName(), getDepartmentCode(), getDescription(), getCollege());
    }

    @Override
    public String toString() {
        return "DepartmentEntity{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                ", description='" + description + '\'' +
                ", college=" + college +
                '}';
    }
}
