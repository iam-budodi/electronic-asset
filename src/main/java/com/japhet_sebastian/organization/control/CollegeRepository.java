package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;import java.util.UUID;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {

    public List<SelectOptions> selectProjection() {
        return find("SELECT ce.collegeId, ce.collegeName FROM College ce")
                .project(SelectOptions.class)
                .list();
    }
}
