package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "colleges",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_college_name_code",
                        columnNames = {"college_name", "college_code"})
        }
)
@NamedQueries({
        @NamedQuery(
                name = "College.name",
                query = "FROM College WHERE LOWER(collegeName) = :name")
})
@Schema(description = "Department representation")
public class College extends PanacheEntity {

    @NotNull
    @Schema(required = true)
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "should include only letters ' and - special characters")
    @Column(name = "college_name", length = 64, nullable = false)
    public String collegeName;

    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "should include only letters, digit, space and underscore")
    @Column(name = "college_code")
    public String collegeCode;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "college_address_fk_constraint", foreignKeyDefinition = ""))
    public Address location;
}
