package com.japhet_sebastian.employee;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.control.AddressRepository;
import com.japhet_sebastian.organization.control.DepartmentRepository;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EmployeeService implements EmployeeInterface {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    EmployeeMapper employeeMapper;

    public List<EmployeeDto> listEmployees(EmployeePage employeePage) {
        List<EmployeeEntity> employeeEntities = employeeRepository.allEmployees(employeePage).list();
        return employeeMapper.toDtoList(employeeEntities);
    }

    public Long totalEmployees() {
        return this.employeeRepository.count();
    }

    public Optional<EmployeeDto> getEmployee(@NotNull String employeeId) {
        return this.employeeRepository.findEmployee(employeeId).map(employeeMapper::toDto);
    }

    public List<EmployeeDto> departmentsReport(LocalDate startDate, LocalDate endDate) {
        List<EmployeeEntity> employeeEntities = employeeRepository.reporting(startDate, endDate).list();
        return employeeMapper.toDtoList(employeeEntities);
    }

    public List<SelectOptions> selected() {
        return this.employeeRepository.selectOptions();
    }

    public void saveEmployee(@Valid EmployeeDto employeeDto) {
        if (departmentExists(employeeDto)) {
            employeeRepository.searchByEmailOrPhone(employeeDto.getEmail(), employeeDto.getMobile())
                    .ifPresent(employeeEntity -> {
                        throw new ServiceException("Employee exists");
                    });

            EmployeeEntity employeeEntity = employeeMapper.toEntity(employeeDto);
            employeeRepository.persist(employeeEntity);
            this.employeeMapper.partialDtoUpdate(employeeEntity, employeeDto);
        }
    }

    public void updateEmployee(@Valid EmployeeDto employeeDto) {
        EmployeeEntity employeeEntity = getEmployeeEntity(employeeDto.employeeId);
        employeeEntity.setDepartment(getDepartmentEntity(employeeDto));
        employeeEntity = employeeMapper.partialUpdate(employeeDto, employeeEntity);
        employeeRepository.persist(employeeEntity);
        employeeMapper.partialDtoUpdate(employeeEntity, employeeDto);
    }

    public void deleteEmployee(@NotNull String employeeId) {
        AddressEntity addressEntity = getEmployeeEntity(employeeId).getAddress();
        this.employeeRepository.delete(getEmployeeEntity(employeeId));
        this.addressRepository.delete(addressEntity);
    }

    private DepartmentEntity getDepartmentEntity(EmployeeDto employeeDto) {
        return departmentEntityOptional(employeeDto)
                .orElseThrow(() -> new ServiceException("Could not find department for associated employee"));
    }

    private Boolean departmentExists(EmployeeDto employeeDto) {
        return departmentEntityOptional(employeeDto).map(department -> true)
                .orElseThrow(() -> new ServiceException("Could not find department for associated employee"));
    }

    private Optional<DepartmentEntity> departmentEntityOptional(EmployeeDto employeeDto) {
        String departmentName = employeeDto.getDepartment().getDepartmentName();
        return departmentRepository.checkDepartmentByName(departmentName);
    }

    private EmployeeEntity getEmployeeEntity(String employeeId) {
        return employeeRepository.findEmployee(employeeId)
                .orElseThrow(() -> new ServiceException("No employee found for employeeId[%s]", employeeId));
    }
}
