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
import org.jboss.logging.Logger;

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

    @Inject
    Logger LOGGER;

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
        addressRepository.persist(collegeEntity.getAddress());
        collegeRepository.persist(collegeEntity);
        collegeMapper.partialDtoUpdate(collegeEntity, collegeDto);
    }

    public void updateCollege(@Valid CollegeDto collegeDto) {
        CollegeEntity collegeEntity = checkCollege(collegeDto.getCollegeId());
        collegeEntity = collegeMapper.partialEntityUpdate(collegeDto, collegeEntity);
        collegeEntity.getAddress().setAddressId(collegeEntity.getCollegeId());
        addressRepository.persist(collegeEntity.getAddress());
        collegeRepository.persist(collegeEntity);
        collegeMapper.partialDtoUpdate(collegeEntity, collegeDto);
    }

    public void deleteCollege(@NotNull String collegeId) {
        CollegeEntity collegeEntity = checkCollege(collegeId);
        AddressEntity addressEntity = collegeEntity.getAddress();
        collegeRepository.delete(collegeEntity);
        addressRepository.delete(addressEntity);
    }

    private CollegeEntity checkCollege(String supplierId) {
        return collegeRepository.singleCollege(supplierId)
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", supplierId));
    }
}