package com.assets.management.assets.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.assets.management.assets.model.Department;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DepartmentServiceTest {

	private static final String    DEFAULT_NAME = "Technology";
	private static final String    UPDATED_NAME = "Technology (updated)";
	private static final String    DEFAULT_DESCRIPTION = "Technology functions";
	private static final String    UPDATED_DESCRIPTION = "Technology functions (updated)";

	@Inject
	DepartmentService service;
	
	// @Test 
	void shouldInsertDepartment() {
		Department department = new Department();
		department.name = DEFAULT_NAME;
		department.description = DEFAULT_DESCRIPTION;
		
		service.insertDepartment(department, null);
		assertTrue(true);
		// fail("Not yet implemented");
	}
	
}
