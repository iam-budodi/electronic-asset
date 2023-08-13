package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.entity.*;
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
public class CollegeService {

    @Inject
    CollegeRepository collegeRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    CollegeMapper collegeMapper;

    @Inject
    CollegeAddressMapper collegeAddressMapper;

    public List<CollegeAddress> listColleges(PageRequest pageRequest) {
        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");

        List<AddressEntity> addressEntities = this.addressRepository.pageOrListAll(pageRequest);
        return this.collegeAddressMapper.toCollegeAddressList(addressEntities);
    }

    public Optional<CollegeAddress> getCollege(@NotNull String collegeId) {
        return this.addressRepository.findAddress(collegeId)
                .map(this.collegeAddressMapper::toCollegeAddress);
    }

    public Long totalColleges() {
        return collegeRepository.count();
    }

    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }

    public void addCollege(@Valid CollegeAddress collegeAddress) {
        CollegeEntity collegeEntity = this.collegeAddressMapper.toCollegeEntity(collegeAddress);
        this.collegeRepository.persist(collegeEntity);

        AddressEntity addressEntity = this.collegeAddressMapper.toAddressEntity(collegeAddress);
        addressEntity.setCollege(collegeEntity);
        this.addressRepository.persist(addressEntity);
        this.collegeAddressMapper.updateCollegeAddressFromCollegeEntity(collegeEntity, collegeAddress);
    }

    @Transactional
    public void updateCollege(@Valid College college) {
        Log.debug("Updating College: {}" + college);
        if (Objects.isNull(college.getCollegeId())) {
            throw new ServiceException("Invalid object, customerId is missing");
        }

        CollegeEntity collegeEntity = findById(college.getCollegeId());
        this.collegeMapper.updateEntityFromDomain(college, collegeEntity);
        this.collegeRepository.persist(collegeEntity);
        collegeMapper.updateCollegeFromCollegeEntity(collegeEntity, college);
    }

    public Boolean deleteCollege(@NotNull String collegeId) {
        College college = this.collegeMapper.toCollege(findById(collegeId));
        if (Objects.nonNull(college) && Objects.nonNull(college.getCollegeId()))
            this.addressRepository.deleteById(UUID.fromString(college.getCollegeId()));

        return this.collegeRepository.deleteById(UUID.fromString(college.getCollegeId()));
    }

    private CollegeEntity findById(String collegeId) {
        return this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));
    }
}
