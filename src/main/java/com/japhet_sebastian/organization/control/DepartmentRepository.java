package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentEntity;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepositoryBase<DepartmentEntity, UUID> {

    @Inject
    DepartmentMapper departmentMapper;

    public List<DepartmentDetail> departments(OrgPage orgPage) {
        if (orgPage.getSearch() != null)
            orgPage.setSearch("%" + orgPage.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM Department d LEFT JOIN FETCH  d.college c LEFT JOIN FETCH c.address " +
                "WHERE :search IS NULL OR LOWER(d.departmentName) LIKE :search " +
                "OR LOWER(d.departmentCode) LIKE :search";

        return find(query, Sort.by("c.collegeName"), Parameters.with("search", orgPage.getSearch()))
                .page(Page.of(orgPage.getPageNumber(), orgPage.getPageSize()))
                .stream().map(this.departmentMapper::toDepartmentDetail)
                .collect(Collectors.toList());

    }

    public Optional<DepartmentDetail> findDepartment(String departmentId) {
        return getById(departmentId)
                .map(this.departmentMapper::toDepartmentDetail);
    }

    public List<SelectOptions> selectProjections() {
        return find("SELECT d.departmentId, d.departmentName FROM Department d")
                .project(SelectOptions.class)
                .list();
    }

//    public List<DepartmentEntity> getDepartmentByCollegeId(String collegeId) {
//        return find("FROM Department d LEFT JOIN FETCH d.college c " +
//                "WHERE c.collegeId = :collegeId", Parameters.with("collegeId", UUID.fromString(collegeId)))
//                .list();
//
//    }

    public void saveDepartment(@Valid DepartmentInput departmentInput) {
        if (checkDepartmentByName(departmentInput.getDepartmentName()).isPresent())
            throw new ServiceException("Department with same name already exists");

        DepartmentEntity departmentEntity = this.departmentMapper.toDepartmentEntity(departmentInput);
        persist(departmentEntity);
        this.departmentMapper.updateDepartmentInputFromDepartmentEntity(departmentEntity, departmentInput);
    }


    public void updateDepartment(@Valid DepartmentInput department) {
        String departmentId = department.getDepartmentId();
        DepartmentEntity departmentEntity = getById(departmentId)
                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));

        this.departmentMapper.updateDepartmentEntityFromDepartmentInput(department, departmentEntity);
        persist(departmentEntity);
        this.departmentMapper.updateDepartmentInputFromDepartmentEntity(departmentEntity, department);
    }

    public void deleteDepartment(@NotNull String departmentId) {
        DepartmentEntity departmentEntity = getById(departmentId)
                .orElseThrow(() -> new ServiceException("No department found for departmentId[%s]", departmentId));
        delete(departmentEntity);
    }

    private Optional<DepartmentEntity> getById(String departmentId) {
        return find("FROM Department d LEFT JOIN FETCH d.college c LEFT JOIN FETCH c.address " +
                "WHERE d.departmentId = ?1", UUID.fromString(departmentId)).firstResultOptional();
    }

    public Optional<DepartmentEntity> checkDepartmentByName(String departmentName) {
        return find("#Department.getName", Parameters.with("name", departmentName.toLowerCase(Locale.ROOT)))
                .firstResultOptional();
    }
}
