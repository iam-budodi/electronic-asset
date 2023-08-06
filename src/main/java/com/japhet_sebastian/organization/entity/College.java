package com.japhet_sebastian.organization.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

public class College {

    private UUID collegeId;

    @NotEmpty(message = "{College.name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String collegeName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    private String collegeCode;

    private Address location;

    public College() {
    }

    public UUID getCollegeId() {
        return collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getCollegeCode() {
        return collegeCode;
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
                "collegeId='" + collegeId + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
                ", location=" + location +
                '}';
    }
}
