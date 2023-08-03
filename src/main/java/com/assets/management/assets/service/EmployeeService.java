package com.assets.management.assets.service;

import com.assets.management.assets.client.QRGeneratorServiceProxy;
import com.assets.management.assets.model.entity.Employee;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {

    @Inject
    @RestClient
    QRGeneratorServiceProxy generatorProxy;

    @Inject
    Logger LOG;

    public Employee addEmployee(@Valid Employee employee) {
        employee.address.employee = employee;
        employee.address.id = employee.id;
        Employee.persist(employee);
        return employee;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Employee> listEmployees(String searchValue, LocalDate date, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sortVariable = String.format("e.%s", column);
        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT e FROM Employee e LEFT JOIN e.department d LEFT JOIN e.address a LEFT JOIN d.college " +
                "WHERE (:searchValue IS NULL OR LOWER(e.firstName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(e.lastName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(e.workId) LIKE :searchValue " +
                "OR LOWER(e.firstName) || ' ' || LOWER(e.lastName) LIKE :searchValue " +
                "OR LOWER(e.email) LIKE :searchValue) ";

        if (date != null) queryString += "AND e.hireDate = :date";
        else queryString += "AND (:date IS NULL OR e.hireDate = :date)";

        LOG.info("SORT VARIABLE : " + sortVariable + " AND DIRECTION " + sortDirection);
        return Employee.find(
                queryString,
//                Sort.by("e.firstName").and("e.lastName").and("e.hireDate"),
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue).and("date", date)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Employee> unPaginatedList(LocalDate startDate, LocalDate endDate) {
        String queryString = "SELECT e FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.status LEFT JOIN FETCH e.address LEFT JOIN FETCH d.college " +
                "WHERE e.registeredAt BETWEEN :startDate AND :endDate";

        return Employee.find(queryString, Sort.by("e.firstName", Sort.Direction.Descending),
                Parameters.with("startDate", startDate).and("endDate", endDate)).list();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Employee> findById(@NotNull Long employeeId) {
        return Employee.find(
                        "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH d.college LEFT JOIN FETCH e.address LEFT JOIN FETCH e.status "
                                + "WHERE e.id = :employeeId ", Parameters.with("employeeId", employeeId))
                .firstResultOptional();
    }

    public void updateEmployee(@Valid Employee employee, @NotNull Long empId) {
        Employee.findByIdOptional(empId)
                .map(found -> Panache.getEntityManager().merge(employee))
                .orElseThrow(() -> new NotFoundException("Employee dont exist"));
    }

    public void deleteEmployee(@NotNull Long empId) {
        Panache.getEntityManager()
                .getReference(Employee.class, empId)
                .delete();
    }
}
