package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.CollegeDto;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.organization.entity.CollegeMapper;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CollegeService implements CollegeInterface {

    @Inject
    CollegeRepository collegeRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    CollegeMapper collegeMapper;


    public List<CollegeDto> listColleges(OrgPage orgPage) {
        List<CollegeEntity> collegeEntities = collegeRepository.allColleges(orgPage).list();
        return collegeMapper.toDtoList(collegeEntities);
    }

    public Optional<CollegeDto> getCollege(@NotNull String collegeId) {
        return collegeRepository.singleCollege(collegeId).map(collegeMapper::toDto);
    }

    public Long totalColleges() {
        return collegeRepository.count();
    }

    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }

    public void saveCollege(@Valid CollegeDto collegeDto) {
        collegeRepository.findByEmailOrPhone(collegeDto.getCollegeName(), collegeDto.getCollegeCode())
                .ifPresent(employeeEntity -> {
                    throw new ServiceException("College already exists");
                });

        CollegeEntity collegeEntity = collegeMapper.toEntity(collegeDto);
        collegeRepository.persist(collegeEntity);
        collegeMapper.partialDtoUpdate(collegeEntity, collegeDto);
    }

    public void updateCollege(@Valid CollegeDto collegeDto) {
        CollegeEntity collegeEntity = getCollegeEntity(collegeDto.getCollegeId());
        collegeEntity = collegeMapper.partialEntityUpdate(collegeDto, collegeEntity);
        collegeRepository.persist(collegeEntity);
        collegeMapper.partialDtoUpdate(collegeEntity, collegeDto);
    }

    public void deleteCollege(@NotNull String collegeId) {
        AddressEntity addressEntity = getCollegeEntity(collegeId).getAddress();
        collegeRepository.delete(getCollegeEntity(collegeId));
        addressRepository.delete(addressEntity);
    }

    private CollegeEntity getCollegeEntity(String supplierId) {
        return collegeRepository.singleCollege(supplierId)
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", supplierId));
    }
}