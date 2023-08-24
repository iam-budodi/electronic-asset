package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

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
        return this.employeeRepository.allEmployees(pageRequest)
                .stream()
                .map(employee -> this.employeeMapper.toEmployeeDetail(employee))
                .collect(Collectors.toList());

    }

    public Long totalEmployees() {
        return this.employeeRepository.count();
    }


//    public Employee addEmployee(@Valid Employee employee) {
//        employee.address.employee = employee;
//        employee.address.id = employee.id;
//        Employee.persist(employee);
//        return employee;
//    }

//
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public List<Employee> unPaginatedList(LocalDate startDate, LocalDate endDate) {
//        String queryString = "SELECT e FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.status LEFT JOIN FETCH e.address LEFT JOIN FETCH d.college " +
//                "WHERE e.registeredAt BETWEEN :startDate AND :endDate";
//
//        return Employee.find(queryString, Sort.by("e.firstName", Sort.Direction.Descending),
//                Parameters.with("startDate", startDate).and("endDate", endDate)).list();
//    }
//
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public Optional<Employee> findById(@NotNull Long employeeId) {
//        return Employee.find(
//                        "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH d.college LEFT JOIN FETCH e.address LEFT JOIN FETCH e.status "
//                                + "WHERE e.id = :employeeId ", Parameters.with("employeeId", employeeId))
//                .firstResultOptional();
//    }
//
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
