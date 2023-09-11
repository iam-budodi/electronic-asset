package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentDto;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentMapper;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DepartmentService implements DepartmentInterface {

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    DepartmentMapper departmentMapper;

    public List<DepartmentDto> listDepartments(OrgPage orgPage) {
        List<DepartmentEntity> departmentEntities = departmentRepository.departments(orgPage).list();
        return departmentMapper.toDtoList(departmentEntities);
    }

    public Long totalDepartments() {
        return departmentRepository.count();
    }

    public Optional<DepartmentDto> getDepartment(@NotNull String departmentId) {
        return this.departmentRepository.findDepartment(departmentId).map(departmentMapper::toDto);
    }

    public List<SelectOptions> selected() {
        return this.departmentRepository.selectProjections();
    }

    public void saveDepartment(@Valid DepartmentDto departmentDto) {
        departmentRepository.checkDepartmentByName(departmentDto.getDepartmentName())
                .ifPresent(departmentEntity -> {
                    throw new ServiceException("Department with same name already exists");
                });

        DepartmentEntity departmentEntity = departmentMapper.toEntity(departmentDto);
        departmentRepository.persist(departmentEntity);
        departmentMapper.partialDtoUpdate(departmentEntity, departmentDto);
    }

    public void updateDepartment(@Valid DepartmentDto departmentDto) {
        DepartmentEntity departmentEntity = checkDepartment(departmentDto.getDepartmentId());
        departmentEntity = departmentMapper.partialEntityUpdate(departmentDto, departmentEntity);
        departmentRepository.persist(departmentEntity);
        this.departmentMapper.partialDtoUpdate(departmentEntity, departmentDto);
    }

    public void deleteDepartment(@NotNull String departmentId) {
        DepartmentEntity departmentEntity = checkDepartment(departmentId);
        this.departmentRepository.delete(departmentEntity);
    }

    private DepartmentEntity checkDepartment(String departmentId) {
        return departmentRepository.findDepartment(departmentId)
                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));
    }
}
