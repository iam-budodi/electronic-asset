package com.japhet_sebastian.organization.entity;

import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public class DepartmentInput extends DepartmentUpdate {

    @NotEmpty(message = "{College.identifier.required}")
    private String collegeId;

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentInput that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getCollegeId(), that.getCollegeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCollegeId());
    }

    @Override
    public String toString() {
        return "DepartmentInput{" +
                "collegeId='" + collegeId + '\'' +
                '}';
    }
}
