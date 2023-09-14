package com.japhet_sebastian.organization.entity;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface AddressMapper {
    AddressEntity toEntity(AddressDto addressDto);

    AddressDto toDto(AddressEntity addressEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(AddressDto addressDto, @MappingTarget AddressEntity addressEntity);

    AddressEntity toEntity(AddressEntity addressEntity);

}