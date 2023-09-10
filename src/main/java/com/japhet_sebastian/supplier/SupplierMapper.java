package com.japhet_sebastian.supplier;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.AddressMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        uses = {AddressMapper.class})
public interface SupplierMapper {
    SupplierEntity toSupplierEntity(SupplierDto supplierDto);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "registeredAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    List<SupplierDto> toListDto(List<SupplierEntity> supplierEntity);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "registeredAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
    SupplierDto toDto(SupplierEntity supplierEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SupplierEntity partialEntityUpdate(SupplierDto supplierDto, @MappingTarget SupplierEntity supplierEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(SupplierEntity supplierEntity, @MappingTarget SupplierDto supplierDto);

    @AfterMapping()
    default void toString(SupplierEntity supplierEntity, @MappingTarget SupplierDto supplierDto) {
        AddressEntity address = supplierEntity.getAddress();
        supplierDto.setSupplierAddress(address.street + " " + address.district + ", " + address.city);
    }
}