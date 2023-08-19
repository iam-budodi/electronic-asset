package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.*;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class DepartmentService implements DepartmentInterface {

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    DepartmentDetailMapper departmentDetailMapper;

    @Inject
    DepartmentMapper departmentMapper;

    public List<DepartmentDetail> listDepartments(PageRequest pageRequest) {
        return this.departmentRepository.departments(pageRequest)
                .stream()
                .map(department -> {
                    String addressId = department.getCollege().getCollegeId();
                    Address address = this.addressRepository.findAddress(addressId)
                            .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", addressId));
                    return this.departmentDetailMapper.toDepartmentDetails(department, address);
                }).collect(Collectors.toList());
    }

    public Long totalDepartments() {
        return departmentRepository.count();
    }

    public Optional<DepartmentDetail> getDepartment(@NotNull String departmentId) {
        return this.departmentRepository.findDepartment(departmentId)
                .stream()
                .map(department -> {
                    String addressId = department.getCollege().getCollegeId();
                    Address address = this.addressRepository.findAddress(addressId)
                            .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", addressId));
                    return this.departmentDetailMapper.toDepartmentDetails(department, address);
                }).findFirst();
    }

    public List<SelectOptions> selected() {
        return this.departmentRepository.selectProjections();
    }

    public void addDepartment(@Valid DepartmentInput departmentInput) {
        boolean isDepartmentPresent = this.departmentRepository
                .findDepartmentByName(departmentInput.getDepartmentName()).isPresent();

        if (isDepartmentPresent)
            throw new ServiceException("Invalid object, department with the same name already exists");

        DepartmentEntity departmentEntity = this.departmentDetailMapper
                .toDepartmentEntity(this.departmentMapper.toDepartment(departmentInput));
        this.departmentRepository.persist(departmentEntity);
        this.departmentMapper.updateDepartmentInputFromDepartmentEntity(departmentEntity, departmentInput);
    }

    public void updateDepartment(@Valid DepartmentUpdate departmentUpdate) {
        String departmentId = departmentUpdate.getDepartmentId();
        DepartmentEntity departmentEntity = this.departmentRepository.findByIdOptional(UUID.fromString(departmentId))
                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));

        this.departmentMapper.updateDepartmentEntityFromDepartmentUpdate(departmentUpdate, departmentEntity);
        this.departmentRepository.persist(departmentEntity);
        this.departmentMapper.updateDepartmentUpdateFromDepartmentEntity(departmentEntity, departmentUpdate);
    }

    public void deleteDepartment(@NotNull String departmentId) {
        DepartmentEntity departmentEntity = this.departmentRepository.findByIdOptional(UUID.fromString(departmentId))
                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));
        this.departmentRepository.delete(departmentEntity);
    }
}
