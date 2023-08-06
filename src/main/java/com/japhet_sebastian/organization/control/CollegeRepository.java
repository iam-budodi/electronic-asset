package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {

    public List<CollegeEntity> search(String value) {
        return find(":value IS NULL OR LOWER(collegeName) LIKE :value OR " +
                        "LOWER(collegeCode) LIKE :value",
                Sort.by("collegeName", Sort.Direction.Descending),
                Parameters.with("value", value))
                .list();
    }

    public boolean isCollege(String collegeName) {
        return find("#College.name", Parameters.with("name", collegeName.toLowerCase(Locale.ROOT)))
                .firstResultOptional()
                .isPresent();
    }

    public List<SelectOptions> selectProjection() {
        return find("SELECT c.id, c.collegeName FROM College c LEFT JOIN FETCH c.locations").project(SelectOptions.class).list();
    }
}
