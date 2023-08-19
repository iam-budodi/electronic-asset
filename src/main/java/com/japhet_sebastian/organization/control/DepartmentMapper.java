package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.Department;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.organization.entity.DepartmentUpdate;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    @Mapping(target = "departmentId",
            expression = "java(departmentEntity.getDepartmentId().toString())")
    Department toDepartment(DepartmentEntity departmentEntity);

    @Mapping(target = "college.collegeId", source = "departmentInput.collegeId")
    Department toDepartment(DepartmentInput departmentInput);

    List<Department> toDepartmentList(List<DepartmentEntity> departmentEntities);

    @Mapping(target = "departmentId", ignore = true)
    @InheritInverseConfiguration(name = "toDepartment")
    DepartmentEntity toDepartmentEntity(Department department);

    @InheritInverseConfiguration(name = "toDepartmentList")
    List<DepartmentEntity> toDepartmentEntities(List<Department> departments);


    void updateDepartmentInputFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentInput departmentInput);

//    void updateDepartmentEntityFromDepartment(Department department, @MappingTarget DepartmentEntity departmentEntity);
//
//    void updateDepartmentFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget Department department);


    void updateDepartmentEntityFromDepartmentUpdate(DepartmentUpdate departmentUpdate, @MappingTarget DepartmentEntity departmentEntity);

    void updateDepartmentUpdateFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentUpdate departmentUpdate);


    @AfterMapping
    default void setDepartmentId(Department department, @MappingTarget DepartmentEntity departmentEntity) {
        if (Objects.nonNull(department.getDepartmentId()))
            departmentEntity.setDepartmentId(UUID.fromString(department.getDepartmentId()));
    }
}
