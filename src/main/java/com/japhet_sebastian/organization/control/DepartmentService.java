package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DepartmentService {

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    DepartmentDetailMapper departmentDetailMapper;

    @Inject
    Logger LOGGER;

    public List<DepartmentDetail> listDepartments(PageRequest pageRequest) {
        return departmentRepository.departments(pageRequest)
                .stream()
                .map(department -> {
                    String addressId = department.getCollege().getCollegeId();
                    Address address = this.addressRepository.findAddress(addressId)
                            .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", addressId));
                    return this.departmentDetailMapper.toDepartmentDetails(department, address);
                }).collect(Collectors.toList());
    }

    public Long totalDepartments() {
        return departmentRepository.count();
    }


//    public PanacheQuery<Employee> listDepartments(String searchValue, String column, String direction) {
//        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";
//
////        String sortVariable = Objects.equals(column.toLowerCase(Locale.ROOT), "departmentName")
////                ? String.format("d.%s", column)
////                : String.format("l.%s", column);
//        String sortVariable = String.format("d.%s", column);
//
//        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
//                ? Sort.Direction.Descending
//                : Sort.Direction.Ascending;
//
//        String queryString = "SELECT d FROM Department d LEFT JOIN  d.college c LEFT JOIN c.location l " +
//                "WHERE (:searchValue IS NULL OR LOWER(d.departmentName) LIKE :searchValue " +
//                "OR :searchValue IS NULL OR LOWER(d.departmentCode) LIKE :searchValue) ";
//
////        LOG.info("SORT VARIABLE : " + sortVariable + " AND DIRECTION " + sortDirection);
//        return Employee.find(
//                queryString,
//                Sort.by(sortVariable, sortDirection),
//                Parameters.with("searchValue", searchValue)
//        );
//    }
//
//    public Department insertDepartment(@Valid Department department) {
//        Department.persist(department);
//        return department;
//    }
//
//    public void updateDepartment(@Valid Department dept, @NotNull Long deptId) {
//        findDepartment(deptId).map(foundDept -> Panache.getEntityManager().merge(dept))
//                .orElseThrow(() -> new NotFoundException("Department don't exist"));
//    }
//
//    public void deleteDepartment(@NotNull Long deptId) {
//        Panache.getEntityManager().getReference(Department.class, deptId).delete();
//    }
//
//    public Optional<Department> findDepartment(@NotNull Long deptId) {
//        return Department.findByIdOptional(deptId);
//    }
}
