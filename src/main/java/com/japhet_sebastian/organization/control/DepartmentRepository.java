package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.Department;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepositoryBase<DepartmentEntity, UUID> {

    @Inject
    DepartmentMapper departmentMapper;

    public List<Department> departments(PageRequest pageRequest) {
        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM Department d LEFT JOIN FETCH  d.college c " +
                "WHERE :search IS NULL OR LOWER(d.departmentName) LIKE :search " +
                "OR LOWER(d.departmentCode) LIKE :search";

        List<DepartmentEntity> departmentEntities = find(
                query, Sort.by("c.collegeName"), Parameters.with("search", pageRequest.getSearch()))
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();

        return this.departmentMapper.toDepartmentList(departmentEntities);
    }

    public Optional<Department> findDepartment(String departmentId) {
        return find("FROM Department d LEFT JOIN FETCH d.college c " +
                "WHERE d.departmentId = ?1", UUID.fromString(departmentId))
                .firstResultOptional()
                .map(this.departmentMapper::toDepartment);
    }

    public List<SelectOptions> selectProjections() {
        return find("SELECT d.departmentId, d.departmentName FROM Department d")
                .project(SelectOptions.class)
                .list();
    }

}
