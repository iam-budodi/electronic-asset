package com.japhet_sebastian.employee;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface EmployeeMapper {
    EmployeeEntity toEntity(EmployeeDto employeeDto);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "registeredAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    List<EmployeeDto> toDtoList(EmployeeEntity employeeEntity);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "registeredAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EmployeeDto toDto(EmployeeEntity employeeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EmployeeEntity partialUpdate(EmployeeDto employeeDto, @MappingTarget EmployeeEntity employeeEntity);

    @AfterMapping()
    default void toString(EmployeeEntity employeeEntity, @MappingTarget EmployeeDto employeeDto) {
        AddressEntity address = employeeEntity.getAddress();
        DepartmentEntity department = employeeEntity.getDepartment();
        employeeDto.setFirstName(address.street + " " + address.district + ", " + address.city);
        employeeDto.setEmployeeAddress(address.street + " " + address.district + ", " + address.city);
        employeeDto.setDepartmentName(department.getDepartmentCode() + ": " + department.getDepartmentName());
    }
}