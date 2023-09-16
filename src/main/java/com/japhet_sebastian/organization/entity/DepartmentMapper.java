package com.japhet_sebastian.organization.entity;

import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, uses = CollegeMapper.class)
public interface DepartmentMapper {
    DepartmentEntity toEntity(DepartmentDto departmentDto);

    List<DepartmentDto> toDtoList(List<DepartmentEntity> departmentEntities);

    @Mapping(target = "college", ignore = true)
    DepartmentDto toDto(DepartmentEntity departmentEntity);

    @InheritInverseConfiguration(name = "toDto")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DepartmentEntity partialEntityUpdate(DepartmentDto departmentDto, @MappingTarget DepartmentEntity departmentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialDtoUpdate(DepartmentEntity departmentEntity, @MappingTarget DepartmentDto departmentDto);

    @AfterMapping()
    default void toString(DepartmentEntity departmentEntity, @MappingTarget DepartmentDto departmentDto) {
        CollegeEntity college = departmentEntity.getCollege();

        if (Objects.nonNull(college) && Objects.nonNull(college.getAddress()) && Objects.nonNull(college.getAddress().getAddressId())) {
            departmentDto.setCollegeName(college.getCollegeName() + " " + "(" + college.getCollegeCode() + ")");
            departmentDto.setAddress(college.getAddress().street + " " + college.getAddress().district + ", " + college.getAddress().city);
        }
    }
}