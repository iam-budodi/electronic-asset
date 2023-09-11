package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentDto;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface DepartmentInterface {

    List<DepartmentDto> listDepartments(OrgPage orgPage);

    Optional<DepartmentDto> getDepartment(@NotNull String departmentId);

    Long totalDepartments();

    List<SelectOptions> selected();

    void saveDepartment(@Valid DepartmentDto departmentDto);

    void updateDepartment(@Valid DepartmentDto departmentDto);

    void deleteDepartment(@NotNull String departmentId);
}
