package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    public List<EmployeeDetail> listEmployees(PageRequest pageRequest) {
        return this.employeeRepository.allEmployees(pageRequest);
    }

    public Long totalEmployees() {
        return this.employeeRepository.count();
    }

    public Optional<EmployeeDetail> getEmployee(@NotNull String employeeId) {
        return this.employeeRepository.findEmployee(employeeId);
    }

    public List<EmployeeDetail> departmentsReport(LocalDate startDate, LocalDate endDate) {
        return this.employeeRepository.reporting(startDate, endDate);
    }

//    public Employee addEmployee(@Valid Employee employee) {
//        employee.address.employee = employee;
//        employee.address.id = employee.id;
//        Employee.persist(employee);
//        return employee;
//    }


//    public void updateEmployee(@Valid Employee employee, @NotNull Long empId) {
//        Employee.findByIdOptional(empId)
//                .map(found -> Panache.getEntityManager().merge(employee))
//                .orElseThrow(() -> new NotFoundException("Employee dont exist"));
//    }
//
//    public void deleteEmployee(@NotNull Long empId) {
//        Panache.getEntityManager()
//                .getReference(Employee.class, empId)
//                .delete();
//    }
}
