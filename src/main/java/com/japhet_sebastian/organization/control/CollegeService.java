package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import java.util.*;

@ApplicationScoped
@Transactional
public class CollegeService {

    @Inject
    CollegeRepository collegeRepository;

    @Inject
    CollegeMapper mapper;

    public List<College> listColleges(String searchValue) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";
        return mapper.toDomainList(collegeRepository.search(searchValue));
    }

    public Optional<College> findCollege(@NotNull UUID collegeId) {
        return collegeRepository.findByIdOptional(collegeId)
                .map(mapper::toDomain);
    }

    public boolean collegeExists(@NotNull String collegeName) {
        return collegeRepository.isCollege(collegeName);
    }

    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }

    public void addCollege(@Valid College college) {
//        Log.debug("Saving College: {}", college);
        Log.debug("Saving College: {}");
        CollegeEntity entity = mapper.toEntity(college);
        collegeRepository.persist(entity);
        mapper.updateDomainFromEntity(entity, college);
    }

    public void updateCollege(@Valid College college) {
        Log.debug("Updating College: {}" + college);
        if (Objects.isNull(college.getCollegeId())) {
            throw new ServiceException("Invalid object, customerId is missing");
        }
        CollegeEntity entity = findById(college.getCollegeId());
        mapper.updateEntityFromDomain(college, entity);
        collegeRepository.persist(entity);
        mapper.updateDomainFromEntity(entity, college);
    }

    public void deleteCollege(@NotNull UUID collegeId) {
        CollegeEntity entity = findById(collegeId);
        College domain = mapper.toDomain(entity);
        collegeRepository.deleteById(entity.getCollegeId());
        mapper.updateDomainFromEntity(entity, domain);
    }

    private CollegeEntity findById(@NotNull UUID collegeId) {
        return collegeRepository.findByIdOptional(collegeId)
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));
    }

}
