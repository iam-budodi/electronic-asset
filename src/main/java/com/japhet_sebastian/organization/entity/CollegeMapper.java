package com.japhet_sebastian.organization.entity;

import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {AddressMapper.class})
public interface CollegeMapper {
    CollegeEntity toEntity(CollegeDto collegeDto);

    List<CollegeDto> toDtoList(List<CollegeEntity> collegeEntities);

    @Mapping(target = "address", ignore = true)
    CollegeDto toDto(CollegeEntity collegeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CollegeEntity partialEntityUpdate(CollegeDto collegeDto, @MappingTarget CollegeEntity collegeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(CollegeEntity collegeEntity, @MappingTarget CollegeDto collegeDto);

    @AfterMapping()
    default void toCollegeAddress(CollegeEntity collegeEntity, @MappingTarget CollegeDto collegeDto) {
        if (Objects.nonNull(collegeEntity.getAddress()) && Objects.nonNull(collegeEntity.getAddress().getAddressId())) {
            AddressEntity address = collegeEntity.getAddress();
            collegeDto.setCollegeAddress(address.street + " " + address.district + ", " + address.city);
        }
    }
}