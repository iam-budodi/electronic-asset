package com.japhet_sebastian.organization.entity;

import com.japhet_sebastian.supplier.AddressDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface AddressMapper {
    AddressEntity toEntity(AddressDto addressDto);

    AddressDto toDto(AddressEntity addressEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AddressEntity partialUpdate(AddressDto addressDto, @MappingTarget AddressEntity addressEntity);
}