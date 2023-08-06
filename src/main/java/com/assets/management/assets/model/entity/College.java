package com.assets.management.assets.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "colleges", uniqueConstraints = {@UniqueConstraint(name = "unique_college_name_code",
        columnNames = {"college_name", "college_code"})})
@NamedQueries({@NamedQuery(name = "College.name", query = "FROM College WHERE LOWER(collegeName) = :name")})
@Schema(description = "College representation")
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "college_uuid")
    private UUID collegeId;

    @NotEmpty
    @Schema(required = true)
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "should include only letters ' and - special characters")
    @Column(name = "college_name", length = 64, nullable = false)
    private String collegeName;

    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "should include only letters, digit, space and underscore")
    @Column(name = "college_code")
    private String collegeCode;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "college_address_fk_constraint", foreignKeyDefinition = ""))
    private Address location;

    public College() {
    }

    public UUID getCollegeId() {
        return collegeId;
    }
//
//    public void setCollegeId(UUID collegeId) {
//        this.collegeId = collegeId;
//    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof College college)) return false;
        return Objects.equals(getCollegeId(), college.getCollegeId())
                && Objects.equals(getCollegeName(), college.getCollegeName())
                && Objects.equals(getCollegeCode(), college.getCollegeCode())
                && Objects.equals(getLocation(), college.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCollegeId(), getCollegeName(), getCollegeCode(), getLocation());
    }

    @Override
    public String toString() {
        return "College{" +
                "collegeId=" + collegeId +
                ", collegeName='" + collegeName + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
                ", location=" + location +
                '}';
    }
}
