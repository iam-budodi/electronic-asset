package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {

    @Inject
    Logger LOG;

    public List<CollegeEntity> search(PageRequest pageRequest) {
        List<CollegeEntity> entities = find("SELECT ce FROM College ce LEFT JOIN FETCH ce.location WHERE " +
                        ":value IS NULL OR LOWER(ce.collegeName) LIKE :value OR LOWER(ce.collegeCode) LIKE :value",
                Sort.by("ce.collegeName", Sort.Direction.Descending),
                Parameters.with("value", pageRequest.getSearch()))
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize())).list();

        LOG.info("College Entities : " + entities.toString());
        return entities;
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
