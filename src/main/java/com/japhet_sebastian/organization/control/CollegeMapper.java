package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollegeMapper {

    List<College> toCollegeList(List<CollegeEntity> entities);

    @Mapping(target = "collegeId", expression = "java(entity.getCollegeId().toString())")
    College toCollege(CollegeEntity entity);

    @Mapping(target = "collegeId", ignore = true)
    @InheritInverseConfiguration(name = "toCollege")
    CollegeEntity toCollegeEntity(College domain);

    @InheritInverseConfiguration(name = "toCollegeList")
    List<CollegeEntity> toCollegeEntityList(List<College> domainList);

    void updateEntityFromDomain(College domain, @MappingTarget CollegeEntity entity);

    void updateCollegeFromCollegeEntity(CollegeEntity entity, @MappingTarget College domain);

    @AfterMapping
    default void setEntityId(College domain, @MappingTarget CollegeEntity entity) {
        if (Objects.nonNull(domain.getCollegeId()))
            entity.setCollegeId(UUID.fromString(domain.getCollegeId()));
    }
}
