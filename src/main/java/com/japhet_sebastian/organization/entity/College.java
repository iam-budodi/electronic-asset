package com.japhet_sebastian.organization.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

public class College {

    private String collegeId;

    @NotEmpty(message = "{College.name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String collegeName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    private String collegeCode;

    public College() {
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
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
        if (!(o instanceof College college)) return false;
        return Objects.equals(getCollegeId(), college.getCollegeId()) && Objects.equals(getCollegeName(), college.getCollegeName()) && Objects.equals(getCollegeCode(), college.getCollegeCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCollegeId(), getCollegeName(), getCollegeCode());
    }

    @Override
    public String toString() {
        return "College{" +
                "collegeId='" + collegeId + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
                '}';
    }
}
