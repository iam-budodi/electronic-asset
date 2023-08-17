package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.Department;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentInputMapper {

    @Mapping(target = "college.collegeId", source = "departmentInput.collegeId")
    Department toDepartment(DepartmentInput departmentInput);


    void updateDepartmentInputFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentInput departmentInput);
}
