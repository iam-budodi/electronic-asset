package com.assets.management.assets.service;

import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.HeadOfDepartment;
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
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class HoDService {

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<HeadOfDepartment> listHoDs(String searchValue) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sort = "hod.employee.firstName";

        String queryString = "SELECT hod FROM HeadOfDepartment hod LEFT JOIN  hod.employee e " +
                "WHERE (:searchValue IS NULL OR " +
                "LOWER(e.firstName) LIKE :searchValue OR " +
                "LOWER(e.middleName) LIKE :searchValue OR " +
                "LOWER(e.lastName) LIKE :searchValue OR " +
                "LOWER(e.workId) LIKE :searchValue OR " +
                "LOWER(e.department.departmentName) LIKE :searchValue)";

        return Employee.find(
                queryString,
                Sort.by(sort, Sort.Direction.Descending),
                Parameters.with("searchValue", searchValue)
        );
    }

    public HeadOfDepartment addHoD(@Valid HeadOfDepartment hod) {
        HeadOfDepartment.persist(hod);
        return hod;
    }

    public void updateHoD(@Valid HeadOfDepartment hod, @NotNull Long hodId) {
        getHoD(hodId).map(foundDept -> Panache.getEntityManager().merge(hod))
                .orElseThrow(() -> new NotFoundException("Head of Department don't exist"));
    }

    public void deleteHoD(@NotNull Long hodId) {
        Panache.getEntityManager().getReference(HeadOfDepartment.class, hodId).delete();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<HeadOfDepartment> getHoD(@NotNull Long hodId) {
        return HeadOfDepartment.findByIdOptional(hodId);
    }
}
