package com.japhet_sebastian.organization.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class DepartmentInput {

    private String departmentId;

    @NotEmpty(message = "{Department.field.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String departmentName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    private String departmentCode;

    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String description;

    private String collegeId;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
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
        return Objects.equals(getDepartmentId(), that.getDepartmentId()) && Objects.equals(getDepartmentName(), that.getDepartmentName()) && Objects.equals(getDepartmentCode(), that.getDepartmentCode()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getCollegeId(), that.getCollegeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDepartmentId(), getDepartmentName(), getDepartmentCode(), getDescription(), getCollegeId());
    }

    @Override
    public String toString() {
        return "DepartmentInput{" +
                "departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                ", description='" + description + '\'' +
                ", collegeId='" + collegeId + '\'' +
                '}';
    }
}
