package com.assets.management.assets.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentTest {
	private static final String DEFAULT_NAME = "Technology";
	private static final String UPDATED_NAME = "Technology - updated";
	private static final String DEFAULT_DESCRIPTION = "Technology functions";
	private static Long deptId;

	@Test
	@Order(1)
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
	@Order(2)
	void shouldFindAll() {
		List<Department> depts = Department.findAllOrderByName();
		assertEquals(1, depts.size());
		assertEquals(DEFAULT_NAME, depts.get(0).name);
	}

	@Test
	@Order(3)
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
	@Order(4)
	void shouldQueryWithParameter() {
		Optional<Department> dept = Department.findByName(DEFAULT_NAME);
		assertEquals(DEFAULT_DESCRIPTION, dept.get().description);

		List<Department> depts = Department.list("name = ?1", DEFAULT_NAME);
		assertEquals(1, depts.size());
	}
	
	@Test
	@Order(5)
	void shouldUpdate() {
		Department dept = Department.findById(deptId);
		dept.name = UPDATED_NAME;
		Panache.getEntityManager().merge(dept);
		assertNotEquals(DEFAULT_NAME, dept.name);
		assertTrue(UPDATED_NAME.equals(dept.name));
		assertEquals(DEFAULT_DESCRIPTION, dept.description);
	}
	
	@Test
	@Order(6)
	void shouldDelete() {
		Department.findById(deptId).delete();
		assertEquals(0, Department.listAll().size());
	}
}
