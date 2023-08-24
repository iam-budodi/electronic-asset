package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CollegeService implements CollegeInterface {

    @Inject
    CollegeRepository collegeRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    CollegeMapper collegeMapper;

    public List<CollegeDetail> listColleges(PageRequest pageRequest) {
        return this.collegeRepository.allColleges(pageRequest)
                .stream()
                .map(college -> {
                    String collegeId = college.getCollegeId();
                    Address address = this.addressRepository.findAddress(collegeId)
                            .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", collegeId));
                    return this.collegeMapper.toCollegeDetail(college, address);
                }).collect(Collectors.toList());
    }

    public Optional<CollegeDetail> getCollege(@NotNull String collegeId) {
        return this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
                .stream()
                .map(collegeEntity -> {
                    String collegeStrId = collegeEntity.getCollegeId().toString();
                    Address address = this.addressRepository.findAddress(collegeStrId)
                            .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", collegeStrId));
                    return this.collegeMapper.toCollegeDetail(this.collegeMapper.toCollege(collegeEntity), address);
                }).findFirst();
    }

    public Long totalColleges() {
        return collegeRepository.count();
    }

    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }

    public void addCollege(@Valid CollegeDetail collegeDetail) {
        CollegeEntity collegeEntity = this.collegeMapper.toCollegeEntity(collegeDetail);
        this.collegeRepository.persist(collegeEntity);

        AddressEntity addressEntity = this.collegeMapper.toAddressEntity(collegeDetail);
        addressEntity.setCollege(collegeEntity);
        this.addressRepository.persist(addressEntity);

        this.collegeMapper.updateCollegeDetailFromCollegeEntity(collegeEntity, collegeDetail);
    }

    public void updateCollege(@Valid CollegeDetail collegeDetail) {
        String collegeId = collegeDetail.getCollegeId();
        CollegeEntity collegeEntity = this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));

        this.collegeMapper.updateCollegeEntityFromCollegeDetail(collegeDetail, collegeEntity);
        this.collegeRepository.persist(collegeEntity);

        this.addressRepository.findByIdOptional(collegeEntity.getCollegeId())
                .map(addressEntity -> {
                    this.collegeMapper.updateAddressEntityFromCollegeDetail(collegeDetail, addressEntity);
                    this.addressRepository.persist(addressEntity);
                    return addressEntity;
                });

        collegeMapper.updateCollegeDetailFromCollegeEntity(collegeEntity, collegeDetail);
    }

    public void deleteCollege(@NotNull String collegeId) {
        CollegeEntity collegeEntity = this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));
        AddressEntity addressEntity = this.addressRepository.findByIdOptional(collegeEntity.getCollegeId())
                .stream().findFirst().orElse(null);

        if (Objects.nonNull(addressEntity))
            this.addressRepository.delete(addressEntity);

        this.departmentRepository.getDepartmentByCollegeId(collegeId)
                .forEach(departmentEntity -> this.departmentRepository.deleteById(departmentEntity.getDepartmentId()));

        this.collegeRepository.delete(collegeEntity);
    }
}
