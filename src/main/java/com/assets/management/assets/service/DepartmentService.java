package com.assets.management.assets.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.entity.Department;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DepartmentService {

	public Department insertDepartment(@Valid Department department) {
		Department.persist(department);
		return department;

	}

	public void updateDepartment(@Valid Department dept, @NotNull Long deptId) {
		findDepartment(deptId).map(foundDept -> Panache.getEntityManager().merge(dept))
							.orElseThrow(() -> new NotFoundException("Department don't exist"));
	}

	public void deleteDepartment(@NotNull Long deptId) {
		Panache.getEntityManager().getReference(Department.class, deptId).delete();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Department> findDepartment(@NotNull Long deptId) {
		return Department.findByIdOptional(deptId);
	}

}
