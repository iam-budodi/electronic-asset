package com.assets.management.assets.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Department;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DepartmentService {

	@Inject
	Logger LOG;

	public Department insertDepartment(@Valid Department department) {
		Department.persist(department);
		return department;

	}

	public void updateDepartment(
	        @Valid Department dept, @NotNull Long deptId
	) {
		findDepartment(deptId).map(
		        foundDept -> Panache.getEntityManager().merge(dept)
		).orElseThrow(
				() -> new NotFoundException("Department dont exist")
				);
	}

	public void deleteDepartment(@NotNull Long deptId) {
		Department department = Panache.getEntityManager().getReference(
		        Department.class, deptId
		);
		department.delete();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Department> findDepartment(@NotNull Long deptId) {
		LOG.debug("DEPT ID IN SVC : " + deptId);
		return Department.findByIdOptional(deptId);
	}

}
