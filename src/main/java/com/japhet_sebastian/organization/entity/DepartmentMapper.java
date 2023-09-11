package com.japhet_sebastian.organization.entity;

import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = CollegeMapper.class)
public interface DepartmentMapper {
    DepartmentEntity toEntity(DepartmentDto departmentDto);

    List<DepartmentDto> toDtoList(List<DepartmentEntity> departmentEntities);

    DepartmentDto toDto(DepartmentEntity departmentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DepartmentEntity partialEntityUpdate(DepartmentDto departmentDto, @MappingTarget DepartmentEntity departmentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(DepartmentEntity departmentEntity, @MappingTarget DepartmentDto departmentDto);
}