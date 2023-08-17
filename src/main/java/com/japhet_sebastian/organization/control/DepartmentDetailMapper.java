package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.Department;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentDetailMapper {

    @Mappings({
            @Mapping(target = "departmentId", expression = "java(department.getDepartmentId().toString())"),
            @Mapping(target = "collegeName", source = "address.college.collegeName"),
            @Mapping(target = "departmentName", source = "department.departmentName"),
            @Mapping(target = "departmentCode", source = "department.departmentCode"),
            @Mapping(target = "description", source = "department.description"),
            @Mapping(target = "street", source = "address.street"),
            @Mapping(target = "city", source = "address.city")
    })
    DepartmentDetail toDepartmentDetails(Department department, Address address);

    @Mappings({
            @Mapping(target = "departmentId", ignore = true),
            @Mapping(target = "departmentName", source = "department.departmentName"),
            @Mapping(target = "departmentCode", source = "department.departmentCode"),
            @Mapping(target = "description", source = "department.description"),
            @Mapping(target = "college.collegeId", source = "department.college.collegeId")
    })
    DepartmentEntity toDepartmentEntity(Department department);
}
