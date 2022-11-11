package com.assets.management.assets.service;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Department;
import com.assets.management.assets.model.Item;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DepartmentService {

	@Inject
	Logger LOG;
	
	public URI insertDepartment(
	        @Valid Department department, @Context UriInfo uriInfo
	) {
		Department.persist(department);
		return uriInfo.getAbsolutePathBuilder().path(
		        Long.toString(department.id)
		).build();
	}

	public void updateDepartment(
	        @Valid Department department, @NotNull Long id
	) {
		Panache.getEntityManager().getReference(Department.class, id);
		Panache.getEntityManager().merge(department);
	}

	public void deleteDepartment(@NotNull Long id) {
		Department department = Panache.getEntityManager().getReference(
		        Department.class, id
		);
		department.delete();
	}

//	@Transactional(Transactional.TxType.SUPPORTS)
//	public Department findDepartment(@NotNull Long deptId) { 
//		LOG.debug("DEPT ID IN SVC : " + deptId);
//        Optional<Department> department = Department.findByIdOptional(deptId);
//        return department.orElseThrow(NotFoundException::new);
//	}
	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Department> findDepartment(@NotNull Long deptId) { 
		LOG.debug("DEPT ID IN SVC : " + deptId);
		return Department.findByIdOptional(deptId); 
	}

}
