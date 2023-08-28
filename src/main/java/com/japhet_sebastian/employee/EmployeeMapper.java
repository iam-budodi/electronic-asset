package com.japhet_sebastian.employee;


import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import org.mapstruct.*;

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
            @Mapping(target = "departmentName", source = "employeeEntity.department.departmentName"),
            @Mapping(target = "timeOfService", source = "employeeEntity.timeOfService")
    })
    EmployeeDetail toEmployeeDetail(EmployeeEntity employeeEntity);


    @Mappings({
            @Mapping(target = "employeeId", ignore = true),
            @Mapping(target = "firstName", source = "employee.firstName"),
            @Mapping(target = "middleName", source = "employee.middleName"),
            @Mapping(target = "lastName", source = "employee.lastName"),
            @Mapping(target = "gender", source = "employee.gender"),
            @Mapping(target = "mobile", source = "employee.mobile"),
            @Mapping(target = "email", source = "employee.email"),
            @Mapping(target = "status", source = "employee.status"),
//            @Mapping(target = "departmentName", source = "employee.departmentName"),
            @Mapping(target = "timeOfService", source = "employee.timeOfService"),
            @Mapping(target = "registeredBy", source = "employee.registeredBy"),
            @Mapping(target = "updatedBy", source = "employee.updatedBy")
    })
    EmployeeEntity toEmployeeEntity(Employee employee);


    @Mappings({
            @Mapping(target = "addressId", ignore = true),
            @Mapping(target = "street", source = "employeeDetail.street"),
            @Mapping(target = "district", source = "employeeDetail.district"),
            @Mapping(target = "city", source = "employeeDetail.city"),
            @Mapping(target = "postalCode", source = "employeeDetail.postalCode"),
            @Mapping(target = "country", source = "employeeDetail.country")
    })
    AddressEntity toAddressEntity(Employee employee);

    void updateEmployeeFromEmployeeEntity(EmployeeEntity employeeEntity, @MappingTarget Employee employee);

}
