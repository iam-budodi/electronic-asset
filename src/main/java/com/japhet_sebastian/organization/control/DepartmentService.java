package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.*;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DepartmentService {

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    DepartmentDetailMapper departmentDetailMapper;

    @Inject
    DepartmentMapper departmentMapper;

    @Inject
    DepartmentInputMapper departmentInputMapper;

    @Inject
    Logger LOGGER;

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
        DepartmentEntity departmentEntity = this.departmentDetailMapper
                .toDepartmentEntity(this.departmentInputMapper.toDepartment(departmentInput));
        this.departmentRepository.persist(departmentEntity);
        this.departmentInputMapper.updateDepartmentInputFromDepartmentEntity(departmentEntity, departmentInput);
        LOGGER.info("DEPT : " + departmentInput);
    }
//    public void updateDepartment(@Valid Department dept, @NotNull Long deptId) {
//        findDepartment(deptId).map(foundDept -> Panache.getEntityManager().merge(dept))
//                .orElseThrow(() -> new NotFoundException("Department don't exist"));
//    }
//
//    public void deleteDepartment(@NotNull Long deptId) {
//        Panache.getEntityManager().getReference(Department.class, deptId).delete();
//    }

}
