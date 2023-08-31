package com.japhet_sebastian.employee;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.control.AddressRepository;
import com.japhet_sebastian.organization.control.DepartmentRepository;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepositoryBase<EmployeeEntity, UUID> {

    @Inject
    EmployeeMapper employeeMapper;

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    Logger LOGGER;

    private String getString(PageRequest pageRequest) {
        String query = "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address LEFT JOIN FETCH e.status " +
                "WHERE :search IS NULL OR LOWER(e.firstName) LIKE :search " +
                "OR LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.workId) LIKE :search " +
                "OR LOWER(e.firstName) || ' ' || LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.email) LIKE :search ";

        if (pageRequest.getDate() != null) query += "AND e.hireDate = :date";
        else query += "AND (:date IS NULL OR e.hireDate = :date)";
        return query;
    }

    public List<Employee> allEmployees(PageRequest pageRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", pageRequest.getSearch());
        params.put("date", pageRequest.getDate());

        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");

        String query = getString(pageRequest);
        Sort sort = Sort.by("e.firstName", Sort.Direction.Descending)
                .and("e.lastName", Sort.Direction.Descending)
                .and("e.hireDate", Sort.Direction.Descending);

        return find(query, sort, params)
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .stream().map(this.employeeMapper::toEmployee)
                .collect(Collectors.toList());
    }

    public Optional<Employee> findEmployee(@NotNull String employeeId) {
        return find(employeeQuery(), Parameters.with("employeeId", UUID.fromString(employeeId)))
                .firstResultOptional()
                .map(this.employeeMapper::toEmployee);
    }

    public List<Employee> reporting(LocalDate startDate, LocalDate endDate) {
        return find(reportingQuery(), Sort.by("e.firstName", Sort.Direction.Descending),
                Parameters.with("startDate", startDate).and("endDate", endDate))
                .stream().map(this.employeeMapper::toEmployee)
                .collect(Collectors.toList());
    }

    public void saveEmployee(@Valid Employee employee) {
        DepartmentEntity departmentEntity = departmentRepository
                .checkDepartmentByName(employee.getDepartmentName())
                .orElseThrow(() -> new ServiceException("Could not find department for associated employee"));

        checkByEmailOrPhone(employee.getEmail(), employee.getMobile())
                .ifPresent(employeeEntity -> {
                    throw new ServiceException("Employee exists");
                });

        AddressEntity addressEntity = this.employeeMapper.toAddressEntity(employee);
        this.addressRepository.persist(addressEntity);

        EmployeeEntity employeeEntity = this.employeeMapper.toEmployeeEntity(employee);
        employeeEntity.setDepartment(departmentEntity);
        employeeEntity.setAddress(addressEntity);
        persist(employeeEntity);

        this.employeeMapper.updateEmployeeFromEmployeeEntity(employeeEntity, employee);
    }

    public void updateEmployee(@Valid Employee employee) {
        EmployeeEntity employeeEntity = find(employeeQuery(),
                Parameters.with("employeeId", UUID.fromString(employee.getEmployeeId())))
                .firstResultOptional()
                .orElseThrow(() -> new ServiceException("No employee found for employeeId[%s]", employee.getEmployeeId()));

        this.employeeMapper.updateEmployeeEntityFromEmployee(employee, employeeEntity);
        persist(employeeEntity);

        this.employeeMapper.updateEmployeeFromEmployeeEntity(employeeEntity, employee);
//
//        Employee.findByIdOptional(empId)
//                .map(found -> Panache.getEntityManager().merge(employee))
//                .orElseThrow(() -> new NotFoundException("Employee dont exist"));
    }


    private Optional<EmployeeEntity> checkByEmailOrPhone(String email, String mobile) {
        return find("#Employee.getByEmailOrPhone", Parameters
                .with("email", email).and("mobile", mobile).map())
                .firstResultOptional();
    }

    private String employeeQuery() {
        return "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address LEFT JOIN FETCH e.status " +
                "WHERE e.employeeId = :employeeId";
    }

    private String reportingQuery() {
        return "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address LEFT JOIN FETCH e.status " +
                "WHERE e.registeredAt BETWEEN :startDate AND :endDate";
    }
}
