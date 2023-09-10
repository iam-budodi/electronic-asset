package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DepartmentService implements DepartmentInterface {

    @Inject
    DepartmentRepository departmentRepository;

    public List<DepartmentDetail> listDepartments(OrgPage orgPage) {
        return this.departmentRepository.departments(orgPage);
    }

    public Long totalDepartments() {
        return departmentRepository.count();
    }

    public Optional<DepartmentDetail> getDepartment(@NotNull String departmentId) {
        return this.departmentRepository.findDepartment(departmentId);
    }

    public List<SelectOptions> selected() {
        return this.departmentRepository.selectProjections();
    }

    public void addDepartment(@Valid DepartmentInput departmentInput) {
        this.departmentRepository.saveDepartment(departmentInput);
    }

    public void updateDepartment(@Valid DepartmentInput department) {
        this.departmentRepository.updateDepartment(department);
    }

    public void deleteDepartment(@NotNull String departmentId) {
        this.departmentRepository.deleteDepartment(departmentId);
    }
}
