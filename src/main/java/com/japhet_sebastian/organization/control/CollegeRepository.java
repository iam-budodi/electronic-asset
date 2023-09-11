package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {


    public PanacheQuery<CollegeEntity> allColleges(OrgPage orgPage) {
        if (orgPage.getSearch() != null)
            orgPage.setSearch("%" + orgPage.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM College c LEFT JOIN FETCH c.address " +
                "WHERE :search IS NULL OR LOWER(c.collegeName) LIKE :search " +
                "OR LOWER(c.collegeCode) LIKE :search";

        return find(query, Sort.by("c.collegeName"), Parameters.with("search", orgPage.getSearch()))
                .page(Page.of(orgPage.getPageNumber(), orgPage.getPageSize()));
    }

    public Optional<CollegeEntity> singleCollege(@NotNull String collegeId) {
        return find(collegeQuery(), UUID.fromString(collegeId)).firstResultOptional();
    }

    public List<SelectOptions> selectProjection() {
        return find("SELECT ce.collegeId, ce.collegeName FROM College ce")
                .project(SelectOptions.class).list();
    }

    public Optional<CollegeEntity> findByEmailOrPhone(String name, String code) {
        return find("#College.nameOrCode", Parameters.with("name", name).and("code", code).map())
                .firstResultOptional();
    }

    private String collegeQuery() {
        return "FROM College c LEFT JOIN FETCH c.address WHERE collegeId = ?1";
    }
}
