package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "CDI")
public interface CollegeMapper {


    List<College> toDomainList(List<CollegeEntity> entities);

    College toDomain(CollegeEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    CollegeEntity toEntity(College domain);

    void updateEntityFromDomain(College domain, @MappingTarget CollegeEntity entity);

    void updateDomainFromEntity(CollegeEntity entity, @MappingTarget College domain);
}
