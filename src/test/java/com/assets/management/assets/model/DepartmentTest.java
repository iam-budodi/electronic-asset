package com.assets.management.assets.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Transactional
class DepartmentTest {
	private static final String DEFAULT_NAME        = "Technology"; 
	private static final String DEFAULT_DESCRIPTION = "Technology functions"; 

	private static Long deptId;
	
	@Test
	void shouldPersistDept() {
		Department dept = new Department();
		dept.name = DEFAULT_NAME;
		dept.description = DEFAULT_DESCRIPTION;
		Department.persist(dept);
		
		assertTrue(dept.isPersistent());
		assertNotNull(dept.id);
		
		deptId = dept.id;
	}
	
	@Test
	void shouldFindAll() {
		List<Department> depts = Department.findAllOrderByName();
		assertEquals(1, depts.size());
		assertEquals(DEFAULT_NAME, depts.get(0).name);
	}
	
	@Test
	void shouldFindDepartments() {
		PanacheQuery<Department> deptQuery = Department.find("from Department dp");
		List<Department> depts = deptQuery.list();
		Long nbDepts = deptQuery.count();
		Department firstDept = deptQuery.firstResult();
		Optional<Department> dept = deptQuery.firstResultOptional();
		
		assertEquals(1, depts.size());
		assertEquals(1, nbDepts);
		assertEquals(deptId, firstDept.id);
		assertEquals(DEFAULT_NAME, dept.get().name);
		
	}

	@Test
	void shouldQueryWithParameter() {
		Optional<Department> dept = Department.findByName(DEFAULT_NAME);
		assertEquals(DEFAULT_DESCRIPTION, dept.get().description);
		
		List<Department> depts = Department.list("name = ?1", DEFAULT_NAME);
		assertEquals(1, depts.size());
	} 
}
