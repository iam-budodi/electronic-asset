package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepositoryBase<DepartmentEntity, UUID> {

    public PanacheQuery<DepartmentEntity> departments(OrgPage orgPage) {
        if (orgPage.getSearch() != null)
            orgPage.setSearch("%" + orgPage.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM Department d LEFT JOIN FETCH  d.college c LEFT JOIN FETCH c.address " +
                "WHERE :search IS NULL OR LOWER(d.departmentName) LIKE :search " +
                "OR LOWER(d.departmentCode) LIKE :search";

        return find(query, Sort.by("c.collegeName"), Parameters.with("search", orgPage.getSearch()))
                .page(Page.of(orgPage.getPageNumber(), orgPage.getPageSize()));

    }

    public Optional<DepartmentEntity> findDepartment(String departmentId) {
        return find("FROM Department d LEFT JOIN FETCH d.college c LEFT JOIN FETCH c.address " +
                "WHERE d.departmentId = ?1", UUID.fromString(departmentId)).firstResultOptional();
    }

    public List<SelectOptions> selectProjections() {
        return find("SELECT d.departmentId, d.departmentName FROM Department d")
                .project(SelectOptions.class)
                .list();
    }

    public Optional<DepartmentEntity> checkDepartmentByName(String departmentName) {
        return find("#Department.getName", Parameters.with("name", departmentName.toLowerCase(Locale.ROOT)))
                .firstResultOptional();
    }
}
