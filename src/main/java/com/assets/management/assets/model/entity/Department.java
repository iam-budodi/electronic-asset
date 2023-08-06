package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_department_name",
                        columnNames = {"department_name"})
        }
)
@NamedQueries({
        @NamedQuery(
                name = "Department.getName",
                query = "FROM DepartmentEntity WHERE LOWER(departmentName) = :name")
})
@Schema(description = "Department representation")
public class Department extends PanacheEntity {

    @NotNull
    @Schema(required = true)
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "should include only letters ' and - special characters")
    @Column(name = "department_name", length = 64, nullable = false)
    public String departmentName;

    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "should include only letters, digit, space and underscore")
    @Column(name = "department_code")
    public String departmentCode;

    @Size(min = 1, max = 400)
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "should include only letters ' and - special characters")
    @Column(length = 400)
    public String description;

    @NotNull
    @Schema(required = true)
    @JoinColumn(
            name = "college_fk",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "college_department_fk_constraint"))
    @ManyToOne(fetch = FetchType.LAZY)
    public College college;

//
//    @OneToOne(mappedBy = "department")
//    public HeadOfDepartment headOfDepartment;

    public static List<Department> findAllOrderByName() {
        return listAll(Sort.by("name"));
    }

    public static Optional<Department> findByName(String name) {
        return find(
                "#Department.getName", Parameters.with("name", name.toLowerCase()))
                .firstResultOptional();
    }
}
