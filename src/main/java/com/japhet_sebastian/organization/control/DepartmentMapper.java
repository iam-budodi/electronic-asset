package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import org.mapstruct.*;

import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    @Mappings({
            @Mapping(target = "departmentId", ignore = true),
            @Mapping(target = "departmentName", source = "departmentInput.departmentName"),
            @Mapping(target = "departmentCode", source = "departmentInput.departmentCode"),
            @Mapping(target = "description", source = "departmentInput.description"),
            @Mapping(target = "college.collegeId", source = "departmentInput.collegeId")
    })
    DepartmentEntity toDepartmentEntity(DepartmentInput departmentInput);

    @Mappings({
            @Mapping(target = "departmentId", expression = "java(departmentEntity.getDepartmentId().toString())"),
            @Mapping(target = "collegeName", source = "departmentEntity.college.collegeName"),
            @Mapping(target = "departmentName", source = "departmentEntity.departmentName"),
            @Mapping(target = "departmentCode", source = "departmentEntity.departmentCode"),
            @Mapping(target = "description", source = "departmentEntity.description"),
            @Mapping(target = "street", source = "departmentEntity.college.address.street"),
            @Mapping(target = "city", source = "departmentEntity.college.address.city")
    })
    DepartmentDetail toDepartmentDetail(DepartmentEntity departmentEntity);

    void updateDepartmentInputFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentInput departmentInput);

    void updateDepartmentEntityFromDepartmentInput(DepartmentInput departmentInput, @MappingTarget DepartmentEntity departmentEntity);

    @AfterMapping
    default void setDepartmentId(DepartmentInput department, @MappingTarget DepartmentEntity departmentEntity) {
        if (Objects.nonNull(department.getDepartmentId()))
            departmentEntity.setDepartmentId(UUID.fromString(department.getDepartmentId()));
    }
}
