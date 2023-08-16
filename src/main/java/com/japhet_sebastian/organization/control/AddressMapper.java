package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.AddressEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {

    @Mapping(target = "addressId",
            expression = "java(addressEntity.getAddressId().toString())")
    Address toAddress(AddressEntity addressEntity);

    List<Address> toAddressList(List<AddressEntity> addressEntities);

    @Mapping(target = "addressId", ignore = true)
    @InheritInverseConfiguration(name = "toAddress")
    AddressEntity toAddressEntity(Address address);

    @InheritInverseConfiguration(name = "toAddressList")
    List<AddressEntity> toAddressEntities(List<Address> addresses);

    void updateAddressEntityFromAddress(Address address, @MappingTarget AddressEntity addressEntity);

    void updateAddressFromAddressEntity(AddressEntity addressEntity, @MappingTarget Address address);

    @AfterMapping
    default void setAddressEntityId(Address address, @MappingTarget AddressEntity addressEntity) {
        if (Objects.nonNull(address.getAddressId()))
            addressEntity.setAddressId(UUID.fromString(address.getAddressId()));
    }
}
