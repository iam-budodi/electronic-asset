package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {
//
//    @Mapping(target = "departmentId",
//            expression = "java(departmentEntity.getDepartmentId().toString())")
//    Department toDepartment(DepartmentEntity departmentEntity);
//
//    @Mapping(target = "college.collegeId", source = "departmentInput.collegeId")
//    Department toDepartment(DepartmentInput departmentInput);
//
//    List<Department> toDepartmentList(List<DepartmentEntity> departmentEntities);


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

//    @Mapping(target = "departmentId", ignore = true)
//    @InheritInverseConfiguration(name = "toDepartment")
//    DepartmentEntity toDepartmentEntity(Department department);
//
//    @InheritInverseConfiguration(name = "toDepartmentList")
//    List<DepartmentEntity> toDepartmentEntities(List<Department> departments);


    void updateDepartmentInputFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentInput departmentInput);

    void updateDepartmentEntityFromDepartmentInput(DepartmentInput departmentInput, @MappingTarget DepartmentEntity departmentEntity);
//
//    void updateDepartmentFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget Department department);


//    void updateDepartmentEntityFromDepartmentUpdate(DepartmentUpdate departmentUpdate, @MappingTarget DepartmentEntity departmentEntity);
//
//    void updateDepartmentUpdateFromDepartmentEntity(DepartmentEntity departmentEntity, @MappingTarget DepartmentUpdate departmentUpdate);


//    @AfterMapping
//    default void setDepartmentId(Department department, @MappingTarget DepartmentEntity departmentEntity) {
//        if (Objects.nonNull(department.getDepartmentId()))
//            departmentEntity.setDepartmentId(UUID.fromString(department.getDepartmentId()));
//    }
}
