package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeAddress;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CollegeService implements CollegeInterface {

    @Inject
    CollegeRepository collegeRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    CollegeMapper collegeMapper;

    @Inject
    CollegeAddressMapper collegeAddressMapper;

    public List<College> listColleges(PageRequest pageRequest) {
        return this.collegeRepository.allColleges(pageRequest);
    }

    public Optional<College> getCollege(@NotNull String collegeId) {
        return this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
                .map(this.collegeMapper::toCollege);
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
