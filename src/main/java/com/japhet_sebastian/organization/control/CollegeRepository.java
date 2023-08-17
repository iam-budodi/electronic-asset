package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.organization.entity.Department;
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
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {

    @Inject
    CollegeMapper collegeMapper;

    public List<College> allColleges(PageRequest pageRequest) {
        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM College c WHERE :search IS NULL OR LOWER(c.collegeName) LIKE :search " +
                "OR LOWER(c.collegeCode) LIKE :search";

        List<CollegeEntity> collegeEntities = find(
                query, Sort.by("c.collegeName"), Parameters.with("search", pageRequest.getSearch()))
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();

        return this.collegeMapper.toCollegeList(collegeEntities);
    }


    public List<SelectOptions> selectProjection() {
        return find("SELECT ce.collegeId, ce.collegeName FROM College ce")
                .project(SelectOptions.class)
                .list();
    }
}
