package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EmployeeService {
//
//    @Inject
//    @RestClient
//    QRGeneratorServiceProxy generatorProxy;

    @Inject
    EmployeeRepository employeeRepository;

    public List<Employee> listEmployees(PageRequest pageRequest) {
        return this.employeeRepository.allEmployees(pageRequest);
    }

    public Long totalEmployees() {
        return this.employeeRepository.count();
    }

    public Optional<Employee> getEmployee(@NotNull String employeeId) {
        return this.employeeRepository.findEmployee(employeeId);
    }

    public List<Employee> departmentsReport(LocalDate startDate, LocalDate endDate) {
        return this.employeeRepository.reporting(startDate, endDate);
    }

    public List<SelectOptions> selected() {
        return this.employeeRepository.selectOptions();
    }

    public void addEmployee(@Valid Employee employee) {
        this.employeeRepository.saveEmployee(employee);
    }

    public void updateEmployee(@Valid Employee employee) {
        this.employeeRepository.updateEmployee(employee);
    }

    public void deleteEmployee(@NotNull String employeeId) {
        this.employeeRepository.deleteEmployee(employeeId);
    }
}
