package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DepartmentService implements DepartmentInterface {

    @Inject
    DepartmentRepository departmentRepository;
//
//    @Inject
//    AddressRepository addressRepository;
//
//    @Inject
//    DepartmentDetailMapper departmentDetailMapper;
//
//    @Inject
//    DepartmentMapper departmentMapper;

    public List<DepartmentDetail> listDepartments(PageRequest pageRequest) {
        return this.departmentRepository.departments(pageRequest);
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
//        String departmentId = department.getDepartmentId();
//        DepartmentEntity departmentEntity = this.departmentRepository.findByIdOptional(UUID.fromString(departmentId))
//                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));
//
//        this.departmentMapper.updateDepartmentEntityFromDepartmentInput(department, departmentEntity);
//        this.departmentRepository.persist(departmentEntity);
//        this.departmentMapper.updateDepartmentInputFromDepartmentEntity(departmentEntity, department);
        this.departmentRepository.updateDepartment(department);
    }

    public void deleteDepartment(@NotNull String departmentId) {
//        DepartmentEntity departmentEntity = this.departmentRepository.findByIdOptional(UUID.fromString(departmentId))
//                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));
//        this.departmentRepository.delete(departmentEntity);
        this.departmentRepository.deleteDepartment(departmentId);
    }
}
