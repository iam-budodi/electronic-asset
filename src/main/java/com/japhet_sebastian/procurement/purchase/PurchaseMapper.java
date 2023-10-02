package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.organization.entity.AddressMapper;
import com.japhet_sebastian.procurement.supplier.SupplierDto;
import com.japhet_sebastian.procurement.supplier.SupplierMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = {SupplierMapper.class, AddressMapper.class})
public interface PurchaseMapper {

    PurchaseEntity toEntity(PurchaseDto purchaseDto);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "supplierName", source = "supplier.companyName")
    @Mapping(target = "supplierEmail", source = "supplier.companyEmail")
    @Mapping(target = "supplierPhone", source = "supplier.companyPhone")
    PurchaseDto toDto(PurchaseEntity purchaseEntity);

    List<PurchaseDto> toListDto(List<PurchaseEntity> purchaseEntities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PurchaseEntity partialEntityUpdate(PurchaseDto purchaseDto, @MappingTarget PurchaseEntity purchaseEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PurchaseDto partialDtoUpdate(PurchaseEntity purchaseEntity, @MappingTarget PurchaseDto purchaseDto);

    default String toSupplierAddress(SupplierDto supplierDto) {
        return supplierDto.getSupplierAddress();
    }
//    @InheritInverseConfiguration(name = "toDto")
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    SupplierEntity partialEntityUpdate(SupplierDto supplierDto, @MappingTarget SupplierEntity supplierEntity);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void partialDtoUpdate(SupplierEntity supplierEntity, @MappingTarget SupplierDto supplierDto);

}