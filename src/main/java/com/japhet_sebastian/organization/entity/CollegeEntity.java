package com.japhet_sebastian.organization.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "College")
@Table(name = "colleges", uniqueConstraints = {@UniqueConstraint(name = "unique_college_name_code",
        columnNames = {"college_name", "college_code"})})
@NamedQueries({@NamedQuery(name = "College.name", query = "FROM College WHERE LOWER(collegeName) = :name")})
@Schema(description = "College Representation")
public class CollegeEntity {
    @Id
    private UUID collegeId;

    @Schema(required = true)
    @NotEmpty(message = "{College.name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "college_name", length = 64, nullable = false)
    private String collegeName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    @Column(name = "college_code")
    private String collegeCode;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "college_uuid", foreignKey = @ForeignKey(name = "college_address_fk_constraint"))
    AddressEntity address;

    public CollegeEntity() {
    }

    public CollegeEntity(UUID collegeId, String collegeName, String collegeCode) {
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.collegeCode = collegeCode;
    }

    public UUID getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(UUID collegeId) {
        this.collegeId = collegeId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollegeEntity entity)) return false;
        return Objects.equals(getCollegeId(), entity.getCollegeId())
                && Objects.equals(getCollegeName(), entity.getCollegeName())
                && Objects.equals(getCollegeCode(), entity.getCollegeCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCollegeId(), getCollegeName(), getCollegeCode() /*, getLocation()*/);
    }

    @Override
    public String toString() {
        return "CollegeEntity{" +
                "collegeId=" + collegeId +
                ", collegeName='" + collegeName + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
//                ", location=" + location +
                '}';
    }
}

