package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepositoryBase<EmployeeEntity, UUID> {

    private String getString(EmployeePage employeePage) {
        String query = "SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address " +
                "WHERE :search IS NULL OR LOWER(e.firstName) LIKE :search " +
                "OR LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.workId) LIKE :search " +
                "OR LOWER(e.firstName) || ' ' || LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.email) LIKE :search ";

        if (employeePage.getDate() != null) query += "AND e.hireDate = :date";
        else query += "AND (:date IS NULL OR e.hireDate = :date)";
        return query;
    }

    public PanacheQuery<EmployeeEntity> allEmployees(EmployeePage employeePage) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", employeePage.getSearch());
        params.put("date", employeePage.getDate());

        if (employeePage.getSearch() != null)
            employeePage.setSearch("%" + employeePage.getSearch().toLowerCase(Locale.ROOT) + "%");

        String query = getString(employeePage);
        Sort sort = Sort.by("e.firstName", Sort.Direction.Descending)
                .and("e.lastName", Sort.Direction.Descending)
                .and("e.hireDate", Sort.Direction.Descending);

        return find(query, sort, params)
                .page(Page.of(employeePage.getPageNumber(), employeePage.getPageSize()));
    }

    public Optional<EmployeeEntity> findEmployee(@NotNull String employeeId) {
        return find(employeeQuery(), Parameters.with("employeeId", UUID.fromString(employeeId)))
                .firstResultOptional();
    }

    public PanacheQuery<EmployeeEntity> reporting(LocalDate startDate, LocalDate endDate) {
        return find(reportingQuery(), Sort.by("e.firstName", Sort.Direction.Descending),
                Parameters.with("startDate", startDate).and("endDate", endDate));
    }

    public List<SelectOptions> selectOptions() {
        return find("SELECT e.id, e.firstName || ' ' || CONCAT(SUBSTRING(e.middleName, 1, 1), '.') || ' ' || " +
                "e.lastName FROM Employee e").project(SelectOptions.class).list();
    }

    public Optional<EmployeeEntity> searchByEmailOrPhone(String email, String mobile) {
        return find("#Employee.getByEmailOrPhone", Parameters.with("email", email).and("mobile", mobile).map())
                .firstResultOptional();
    }


    private String employeeQuery() {
        return "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address " +
                "WHERE e.employeeId = :employeeId";
    }

    private String reportingQuery() {
        return "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address " +
                "WHERE DATE(e.registeredAt) BETWEEN :startDate AND :endDate";
    }
}
