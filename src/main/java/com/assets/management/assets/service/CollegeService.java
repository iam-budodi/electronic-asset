package com.assets.management.assets.service;


import com.assets.management.assets.model.entity.College;
import com.assets.management.assets.model.repository.CollegeRepository;
import com.assets.management.assets.model.valueobject.SelectOptions;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
//@Transactional(Transactional.TxType.REQUIRED)
public class CollegeService {

    @Inject
    CollegeRepository collegeRepository;

    //    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<College> listColleges(String searchValue) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

//        String queryString = "SELECT c FROM College c LEFT JOIN c.location l " +
//                "WHERE (:searchValue IS NULL OR LOWER(c.collegeName) LIKE :searchValue " +
//                "OR :searchValue IS NULL OR LOWER(c.collegeCode) LIKE :searchValue) ";
//
//        return College.find(
//                queryString,
//                Sort.by("c.collegeName", Sort.Direction.Descending),
//                Parameters.with("searchValue", searchValue)
//        );

        return collegeRepository.search(searchValue);


    }

    public void addCollege(@Valid College college) {
        collegeRepository.persist(college);
    }

    public void updateCollege(@Valid College college, @NotNull UUID collegeId) {
        findCollege(collegeId).map(found -> Panache.getEntityManager().merge(college))
                .orElseThrow(() -> new NotFoundException("College don't exist"));


//        findCollege(collegeId).map(found -> {
//                    found = college;
//                    found.
//                })
//                .orElseThrow(() -> new NotFoundException("College don't exist"));


    }

    public Boolean deleteCollege(@NotNull UUID collegeId) {
//        Panache.getEntityManager().getReference(College.class, collegeId).delete();
        return collegeRepository.deleteById(collegeId);
    }

    //    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<College> findCollege(@NotNull UUID collegeId) {
        return collegeRepository.findByIdOptional(collegeId);
    }

    public boolean collegeExists(@NotNull String collegeName) {
        return collegeRepository.isCollege(collegeName);
    }


    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }
}
