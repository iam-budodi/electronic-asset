package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.organization.entity.Department;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
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

    List<Department> toDepartmentList(List<DepartmentEntity> departmentEntities);

    @Mapping(target = "departmentId", ignore = true)
    @InheritInverseConfiguration(name = "toDepartment")
    DepartmentEntity toDepartmentEntity(Department department);

    @InheritInverseConfiguration(name = "toDepartmentList")
    List<DepartmentEntity> toDepartmentEntities(List<Department> departments);


    void updateDepartmentEntityFromDepartment(Department department, @MappingTarget DepartmentEntity departmentEntity);


    @AfterMapping
    default void setDepartmentId(Department department, @MappingTarget DepartmentEntity departmentEntity) {
        if (Objects.nonNull(department.getDepartmentId()))
            departmentEntity.setDepartmentId(UUID.fromString(department.getDepartmentId()));
    }
}
