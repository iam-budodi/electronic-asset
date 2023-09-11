package com.japhet_sebastian.organization.entity;

import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {AddressMapper.class})
public interface CollegeMapper {
    CollegeEntity toEntity(CollegeDto collegeDto);

    @Mapping(target = "address", ignore = true)
    List<CollegeDto> toDtoList(List<CollegeEntity> collegeEntities);

    @Mapping(target = "address", ignore = true)
    CollegeDto toDto(CollegeEntity collegeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CollegeEntity partialEntityUpdate(CollegeDto collegeDto, @MappingTarget CollegeEntity collegeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(CollegeEntity collegeEntity, @MappingTarget CollegeDto collegeDto);

    @AfterMapping()
    default void toString(CollegeEntity collegeEntity, @MappingTarget CollegeDto collegeDto) {
        AddressEntity address = collegeEntity.getAddress();
        collegeDto.setCollegeAddress(address.street + " " + address.district + ", " + address.city);
    }
}