package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface DepartmentInterface {

    List<DepartmentDetail> listDepartments(OrgPage orgPage);

    Optional<DepartmentDetail> getDepartment(@NotNull String departmentId);

    Long totalDepartments();

    List<SelectOptions> selected();

    void addDepartment(@Valid DepartmentInput departmentInput);

    void updateDepartment(@Valid DepartmentInput departmentInput);

    void deleteDepartment(@NotNull String departmentId);
}
