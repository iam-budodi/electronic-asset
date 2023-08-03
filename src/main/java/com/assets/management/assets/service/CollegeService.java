package com.assets.management.assets.service;


import com.assets.management.assets.model.entity.College;
import com.assets.management.assets.model.entity.Department;
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
public class CollegeService {

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<College> listColleges(String searchValue) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String queryString = "SELECT c FROM College c LEFT JOIN c.location l " +
                "WHERE (:searchValue IS NULL OR LOWER(c.collegeName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(c.collegeCode) LIKE :searchValue) ";

        return College.find(
                queryString,
                Sort.by("c.collegeName", Sort.Direction.Descending),
                Parameters.with("searchValue", searchValue)
        );
    }

    public College addCollege(@Valid College college) {
        Department.persist(college);
        return college;
    }

    public void updateCollege(@Valid College college, @NotNull Long collegeId) {
        findCollege(collegeId).map(foundCollege -> Panache.getEntityManager().merge(college))
                .orElseThrow(() -> new NotFoundException("College don't exist"));
    }

    public void deleteCollege(@NotNull Long collegeId) {
        Panache.getEntityManager().getReference(College.class, collegeId).delete();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<College> findCollege(@NotNull Long collegeId) {
        return College.findByIdOptional(collegeId);
    }

}
