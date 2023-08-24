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
            @Mapping(target = "employeeId", expression = "java(employee.getEmployeeId().toString())"),
            @Mapping(target = "firstName", source = "employee.firstName"),
            @Mapping(target = "middleName", source = "employee.middleName"),
            @Mapping(target = "lastName", source = "employee.lastName"),
            @Mapping(target = "gender", source = "employee.gender"),
            @Mapping(target = "mobile", source = "employee.mobile"),
            @Mapping(target = "email", source = "employee.email"),
            @Mapping(target = "status", source = "employee.status"),
            @Mapping(target = "departmentName", source = "employee.departmentName")
    })
    EmployeeDetail toEmployeeDetail(Employee employee);

}
