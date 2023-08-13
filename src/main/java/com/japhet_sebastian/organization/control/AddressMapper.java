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

    List<Address> toDomainList(List<AddressEntity> entities);

    @Mapping(target = "addressId", expression = "java(entity.getAddressId().toString())")
    Address toDomain(AddressEntity entity);

    @Mapping(target = "addressId", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    AddressEntity toEntity(Address domain);

    @InheritInverseConfiguration(name = "toDomainList")
    List<AddressEntity> toEntityList(List<Address> domainList);

    void updateEntityFromDomain(Address domain, @MappingTarget AddressEntity entity);

    void updateAddressFromAddressEntity(AddressEntity entity, @MappingTarget Address domain);

    @AfterMapping
    default void setEntityId(Address domain, @MappingTarget AddressEntity entity) {
        if (Objects.nonNull(domain.getAddressId()))
            entity.setAddressId(UUID.fromString(domain.getAddressId()));
    }
}
