package com.japhet_sebastian.organization.control;


import com.japhet_sebastian.organization.entity.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollegeAddressMapper {

    @Mappings({
            @Mapping(target = "collegeId", expression = "java(collegeEntity.getCollegeId().toString())"),
            @Mapping(target = "collegeName", source = "collegeEntity.collegeName"),
            @Mapping(target = "collegeCode", source = "collegeEntity.collegeCode"),
            @Mapping(target = "address.addressId", expression = "java(addressEntity.getAddressId().toString())"),
            @Mapping(target = "address.street", source = "addressEntity.street"),
            @Mapping(target = "address.ward", source = "addressEntity.ward"),
            @Mapping(target = "address.district", source = "addressEntity.district"),
            @Mapping(target = "address.city", source = "addressEntity.city"),
            @Mapping(target = "address.postalCode", source = "addressEntity.postalCode"),
            @Mapping(target = "address.country", source = "addressEntity.country")
    })
    CollegeAddress toCollegeAddress(CollegeEntity collegeEntity, AddressEntity addressEntity);

    @Mappings({
            @Mapping(target = "collegeId", expression = "java(addressEntity.college.getCollegeId().toString())"),
            @Mapping(target = "collegeName", source = "addressEntity.college.collegeName"),
            @Mapping(target = "collegeCode", source = "addressEntity.college.collegeCode"),
            @Mapping(target = "address.addressId", expression = "java(addressEntity.getAddressId().toString())"),
            @Mapping(target = "address.street", source = "addressEntity.street"),
            @Mapping(target = "address.ward", source = "addressEntity.ward"),
            @Mapping(target = "address.district", source = "addressEntity.district"),
            @Mapping(target = "address.city", source = "addressEntity.city"),
            @Mapping(target = "address.postalCode", source = "addressEntity.postalCode"),
            @Mapping(target = "address.country", source = "addressEntity.country")
    })
    CollegeAddress toCollegeAddress(AddressEntity addressEntity);

    @Mappings({
            @Mapping(target = "collegeId", expression = "java(addressEntities.college.getCollegeId().toString())"),
            @Mapping(target = "collegeName", source = "addressEntities.college.collegeName"),
            @Mapping(target = "collegeCode", source = "addressEntities.college.collegeCode"),
            @Mapping(target = "address.addressId", expression = "java(addressEntities.getAddressId().toString())"),
            @Mapping(target = "address.street", source = "addressEntities.street"),
            @Mapping(target = "address.ward", source = "addressEntities.ward"),
            @Mapping(target = "address.district", source = "addressEntities.district"),
            @Mapping(target = "address.city", source = "addressEntities.city"),
            @Mapping(target = "address.postalCode", source = "addressEntities.postalCode"),
            @Mapping(target = "address.country", source = "addressEntities.country")
    })
    List<CollegeAddress> toCollegeAddressList(List<AddressEntity> addressEntities);

    @Mappings({
            @Mapping(target = "collegeId", ignore = true),
            @Mapping(target = "collegeName", source = "collegeAddress.collegeName"),
            @Mapping(target = "collegeCode", source = "collegeAddress.collegeCode")
    })
    CollegeEntity toCollegeEntity(CollegeAddress collegeAddress);

    @Mappings({
            @Mapping(target = "addressId", ignore = true),
//            @Mapping(target = "supplier", ignore = true),
//            @Mapping(target = "employee", ignore = true),
            @Mapping(target = "street", source = "collegeAddress.address.street"),
            @Mapping(target = "ward", source = "collegeAddress.address.ward"),
            @Mapping(target = "district", source = "collegeAddress.address.district"),
            @Mapping(target = "city", source = "collegeAddress.address.city"),
            @Mapping(target = "postalCode", source = "collegeAddress.address.postalCode"),
            @Mapping(target = "country", source = "collegeAddress.address.country")
    })
    AddressEntity toAddressEntity(CollegeAddress collegeAddress);

    @Mapping(target = "collegeId", expression = "java(collegeEntity.getCollegeId().toString())")
    College toCollege(CollegeEntity collegeEntity);


    @Mapping(target = "addressId", expression = "java(addressEntity.getAddressId().toString())")
    Address toAddress(AddressEntity addressEntity);

    void updateCollegeAddressFromCollegeEntity(CollegeEntity collegeEntity, @MappingTarget CollegeAddress collegeAddress);

    @AfterMapping
    default void setCollegeEntityId(CollegeAddress collegeAddress, @MappingTarget CollegeEntity collegeEntity) {
        if (Objects.nonNull(collegeAddress.getCollegeId()))
            collegeEntity.setCollegeId(UUID.fromString(collegeAddress.getCollegeId()));
    }

    @AfterMapping
    default void setAddressEntityId(CollegeAddress collegeAddress, @MappingTarget AddressEntity addressEntity) {
        if (Objects.nonNull(collegeAddress.getAddress()) && Objects.nonNull(collegeAddress.getAddress().getAddressId()))
            addressEntity.setAddressId(UUID.fromString(collegeAddress.getAddress().getAddressId()));
    }
}
