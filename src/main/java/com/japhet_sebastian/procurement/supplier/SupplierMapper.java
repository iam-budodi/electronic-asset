package com.japhet_sebastian.procurement.supplier;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.AddressMapper;
import com.japhet_sebastian.vo.DateMapper;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {AddressMapper.class, DateMapper.class})
public interface SupplierMapper {
    SupplierEntity toSupplierEntity(SupplierDto supplierDto);

    List<SupplierDto> toListDto(List<SupplierEntity> supplierEntities);

    @Mapping(target = "address", ignore = true)
    SupplierDto toDto(SupplierEntity supplierEntity);

    @InheritInverseConfiguration(name = "toDto")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SupplierEntity partialEntityUpdate(SupplierDto supplierDto, @MappingTarget SupplierEntity supplierEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(SupplierEntity supplierEntity, @MappingTarget SupplierDto supplierDto);

    @AfterMapping()
    default void toString(SupplierEntity supplierEntity, @MappingTarget SupplierDto supplierDto) {
        AddressEntity address = supplierEntity.getAddress();
        if (Objects.nonNull(address))
            supplierDto.setSupplierAddress(address.street + " " + address.district + ", " + address.city);
    }
}