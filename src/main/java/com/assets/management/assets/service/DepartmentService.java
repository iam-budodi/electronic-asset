package com.assets.management.assets.service;

import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DepartmentService {

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Employee> listDepartments(String searchValue, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

//        String sortVariable = Objects.equals(column.toLowerCase(Locale.ROOT), "departmentName")
//                ? String.format("d.%s", column)
//                : String.format("l.%s", column);
        String sortVariable = String.format("d.%s", column);

        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT d FROM Department d LEFT JOIN  d.college c LEFT JOIN c.location l " +
                "WHERE (:searchValue IS NULL OR LOWER(d.departmentName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(d.departmentCode) LIKE :searchValue) ";

//        LOG.info("SORT VARIABLE : " + sortVariable + " AND DIRECTION " + sortDirection);
        return Employee.find(
                queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue)
        );
    }

    public Department insertDepartment(@Valid Department department) {
        Department.persist(department);
        return department;
    }

    public void updateDepartment(@Valid Department dept, @NotNull Long deptId) {
        findDepartment(deptId).map(foundDept -> Panache.getEntityManager().merge(dept))
                .orElseThrow(() -> new NotFoundException("Department don't exist"));
    }

    public void deleteDepartment(@NotNull Long deptId) {
        Panache.getEntityManager().getReference(Department.class, deptId).delete();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Department> findDepartment(@NotNull Long deptId) {
        return Department.findByIdOptional(deptId);
    }

}
