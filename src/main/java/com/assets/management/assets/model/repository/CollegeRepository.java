package com.assets.management.assets.model.repository;


import com.assets.management.assets.model.entity.College;
import com.assets.management.assets.model.valueobject.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<College, UUID> {

    public PanacheQuery<College> search(String value) {
        return find(":value IS NULL OR LOWER(collegeName) LIKE :value OR " +
                        "LOWER(collegeCode) LIKE :value", Sort.by("collegeName", Sort.Direction.Descending),
                Parameters.with("value", value));
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
