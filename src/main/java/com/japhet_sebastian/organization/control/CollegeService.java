package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.PageRequest;
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

    public List<College> listColleges(PageRequest pageRequest) {
        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");
        Log.info("Lower Case Search String : " + pageRequest.getSearch());
        return mapper.toDomainList(collegeRepository.search(pageRequest));
    }

    public Optional<College> findCollege(@NotNull UUID collegeId) {
        return collegeRepository.findByIdOptional(collegeId)
                .map(mapper::toDomain);
    }

    public Long totalColleges() {
        return collegeRepository.count();
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

    public Boolean deleteCollege(@NotNull UUID collegeId) {
        CollegeEntity entity = findById(collegeId);
        College domain = mapper.toDomain(entity);
        boolean deleted = collegeRepository.deleteById(entity.getCollegeId());
        mapper.updateDomainFromEntity(entity, domain);
        return deleted;
    }

    private CollegeEntity findById(UUID collegeId) {
        return collegeRepository.findByIdOptional(collegeId)
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));
    }

}
