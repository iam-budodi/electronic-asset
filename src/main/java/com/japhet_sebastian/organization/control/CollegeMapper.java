package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import org.mapstruct.*;

import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollegeMapper {

    @Mappings({
            @Mapping(target = "collegeId", ignore = true),
            @Mapping(target = "collegeName", source = "collegeDetail.collegeName"),
            @Mapping(target = "collegeCode", source = "collegeDetail.collegeCode")
    })
    CollegeEntity toCollegeEntity(CollegeDetail collegeDetail);

    @Mappings({
            @Mapping(target = "addressId", ignore = true),
            @Mapping(target = "street", source = "collegeDetail.street"),
            @Mapping(target = "district", source = "collegeDetail.district"),
            @Mapping(target = "city", source = "collegeDetail.city"),
            @Mapping(target = "postalCode", source = "collegeDetail.postalCode"),
            @Mapping(target = "country", source = "collegeDetail.country")
    })
    AddressEntity toAddressEntity(CollegeDetail collegeDetail);

    @Mappings({
            @Mapping(target = "collegeId", expression = "java(collegeEntity.getCollegeId().toString())"),
            @Mapping(target = "collegeName", source = "collegeEntity.collegeName"),
            @Mapping(target = "collegeCode", source = "collegeEntity.collegeCode"),
            @Mapping(target = "street", source = "collegeEntity.address.street"),
            @Mapping(target = "postalCode", source = "collegeEntity.address.postalCode"),
            @Mapping(target = "district", source = "collegeEntity.address.district"),
            @Mapping(target = "city", source = "collegeEntity.address.city"),
            @Mapping(target = "country", source = "collegeEntity.address.country")
    })
    CollegeDetail toCollegeDetail(CollegeEntity collegeEntity);

    void updateCollegeDetailFromCollegeEntity(CollegeEntity collegeEntity, @MappingTarget CollegeDetail collegeDetail);

    void updateCollegeEntityFromCollegeDetail(CollegeDetail collegeDetail, @MappingTarget CollegeEntity collegeEntity);

    void updateAddressEntityFromCollegeDetail(CollegeDetail collegeDetail, @MappingTarget AddressEntity addressEntity);

    @AfterMapping
    default void setCollegeEntityId(CollegeDetail collegeDetail, @MappingTarget CollegeEntity collegeEntity) {
        if (Objects.nonNull(collegeDetail.getCollegeId()))
            collegeEntity.setCollegeId(UUID.fromString(collegeDetail.getCollegeId()));
    }

    @AfterMapping
    default void setAddressEntityId(CollegeDetail collegeDetail, @MappingTarget AddressEntity addressEntity) {
        if (Objects.nonNull(collegeDetail.getCollegeId()) && Objects.nonNull(collegeDetail.getCollegeId()))
            addressEntity.setAddressId(UUID.fromString(collegeDetail.getCollegeId()));
    }
}
