package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeInterface {
    List<EmployeeDto> listEmployees(EmployeePage employeePage);

    Optional<EmployeeDto> getEmployee(@NotNull String employeeId);

    Long totalEmployees();

    List<EmployeeDto> departmentsReport(LocalDate startDate, LocalDate endDate);

    List<SelectOptions> selected();

    void saveEmployee(@Valid EmployeeDto employeeDto);

    void updateEmployee(@Valid EmployeeDto employeeDto);

    void deleteEmployee(@NotNull String employeeId);

}
