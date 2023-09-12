package com.japhet_sebastian.employee;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.AddressMapper;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentMapper;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {AddressMapper.class, DepartmentMapper.class})
public interface EmployeeMapper {
    EmployeeEntity toEntity(EmployeeDto employeeDto);

    List<EmployeeDto> toDtoList(List<EmployeeEntity> employeeEntity);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "registeredAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EmployeeDto toDto(EmployeeEntity employeeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EmployeeEntity partialUpdate(EmployeeDto employeeDto, @MappingTarget EmployeeEntity employeeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(EmployeeEntity employeeEntity, @MappingTarget EmployeeDto employeeDto);

    @AfterMapping()
    default void toString(EmployeeEntity employeeEntity, @MappingTarget EmployeeDto employeeDto) {
        AddressEntity address = employeeEntity.getAddress();
        DepartmentEntity department = employeeEntity.getDepartment();
        String middleName = employeeEntity.getMiddleName();

        if (Objects.nonNull(middleName)) middleName = employeeEntity.getMiddleName().charAt(0) + ". ";
        else middleName = " ";

        employeeDto.setFullName(employeeEntity.getFirstName() + " " + middleName + employeeEntity.getLastName());
        employeeDto.setEmployeeAddress(address.street + " " + address.district + ", " + address.city);
//        employeeDto.setDepartmentName(department.getDepartmentName() + "(" + department.getDepartmentCode() + ")");
    }
}