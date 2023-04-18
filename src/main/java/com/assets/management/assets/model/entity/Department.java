package com.assets.management.assets.model.entity;

import java.util.List;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

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
                query = "FROM Department WHERE LOWER(name) = :name")
})
@Schema(description = "Department representation")
public class Department extends PanacheEntity {

    @NotNull
    @Schema(required = true)
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    @Column(name = "department_name", length = 64, nullable = false)
    public String name;

    @NotNull
    @Schema(required = true)
    @Column(name = "department_code")
    public String code;

    @Size(min = 1, max = 400)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    @Column(length = 400)
    public String description;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "department_address_fk_constraint", foreignKeyDefinition = ""))
    public Address location;

    public static List<Department> findAllOrderByName() {
        return listAll(Sort.by("name"));
    }

    public static Optional<Department> findByName(String name) {
        return find(
                "#Department.getName", Parameters.with("name", name.toLowerCase()))
                .firstResultOptional();
    }
}
