package com.japhet_sebastian.employee;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    List<Employee> toEmployeeList(List<EmployeeEntity> employeeEntities);

    @Mappings({
            @Mapping(target = "employeeId", expression = "java(employeeEntity.getEmployeeId().toString())"),
            @Mapping(target = "firstName", source = "employeeEntity.firstName"),
            @Mapping(target = "middleName", source = "employeeEntity.middleName"),
            @Mapping(target = "lastName", source = "employeeEntity.lastName"),
            @Mapping(target = "gender", source = "employeeEntity.gender"),
            @Mapping(target = "mobile", source = "employeeEntity.mobile"),
            @Mapping(target = "email", source = "employeeEntity.email"),
            @Mapping(target = "status", source = "employeeEntity.status"),
            @Mapping(target = "departmentName", source = "employeeEntity.department.departmentName")
    })
    EmployeeDetail toEmployeeDetail(EmployeeEntity employeeEntity);

}
