package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.*;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepositoryBase<EmployeeEntity, UUID> {

    @Inject
    EmployeeMapper employeeMapper;

    @Inject
    Logger LOGGER;

    private String getString(PageRequest pageRequest) {
        LOGGER.info("DATE : " + pageRequest.getDate());
        String query = "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN d.college " +
                "WHERE :search IS NULL OR LOWER(e.firstName) LIKE :search " +
                "OR LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.workId) LIKE :search " +
                "OR LOWER(e.firstName) || ' ' || LOWER(e.lastName) LIKE :search " +
                "OR LOWER(e.email) LIKE :search ";

        if (pageRequest.getDate() != null) query += "AND e.hireDate = :date";
        else query += "AND (:date IS NULL OR e.hireDate = :date)";
        LOGGER.info("QUERY: " + query);
        return query;
    }

    public List<Employee> allEmployees(PageRequest pageRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", pageRequest.getSearch());
        params.put("date", pageRequest.getDate());

        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");

        String query = getString(pageRequest);

        List<EmployeeEntity> employeeEntities = find(query, Sort
                        .by("e.firstName", Sort.Direction.Descending).and("e.lastName", Sort.Direction.Descending)
                        .and("e.hireDate", Sort.Direction.Descending),
                params)
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();

        LOGGER.info("LIST : " + employeeEntities);

        return this.employeeMapper.toEmployeeList(employeeEntities);
    }

    public boolean checkByEmailOrPhone(String email, String mobile) {
        return find(
                "#Employee.getEmailOrPhone", Parameters.with("email", email).and("mobile", mobile).map())
                .firstResultOptional().isPresent();
    }
}
