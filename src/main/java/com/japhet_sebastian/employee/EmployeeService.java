package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger;

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

    @Inject
    EmployeeMapper employeeMapper;

    @Inject
    Logger LOGGER;

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

    public void addEmployee(@Valid Employee employee) {
        this.employeeRepository.saveEmployee(employee);
    }

    public void updateEmployee(@Valid Employee employee) {
        this.employeeRepository.updateEmployee(employee);
    }
//    public void deleteEmployee(@NotNull Long empId) {
//        Panache.getEntityManager()
//                .getReference(Employee.class, empId)
//                .delete();
//    }
}
