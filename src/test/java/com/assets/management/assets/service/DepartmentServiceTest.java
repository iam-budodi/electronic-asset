package com.assets.management.assets.service;

import com.assets.management.assets.model.entity.Department;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentServiceTest {

    private static final String DEFAULT_NAME = "Technology";
    private static final String UPDATED_NAME = "Technology - updated";
    private static final String DEFAULT_DESCRIPTION = "Technology functions";
    private static final String UPDATED_DESCRIPTION = "Technology functions - updated";
    private static long deptId;

    @Inject
    DepartmentService service;

    @Test
    @Order(1)
    void shouldNotGetUnknownDepartment() {
        Long randomId = new Random().nextLong();
        Optional<Department> department = service.findDepartment(randomId);
        assertFalse(department.isPresent());
    }

    @Test
    @Order(2)
    void shouldThrowExceptionOnInsertingNullDepartmentObject() {
        Department department = new Department();
        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> service.insertDepartment(department));
        assertEquals(null, thrown.getCause());

    }

    @Test
    @Order(3)
    void shouldThrowExceptionOnInsertingNullDepartmentName() {
        Department department = new Department();
        department.departmentName = null;
        department.description = DEFAULT_DESCRIPTION;
        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> service.insertDepartment(department));
        assertEquals(null, thrown.getCause());

    }

    @Test
    @Order(4)
    void shouldInsertDepartment() {
        Department department = new Department();
        department.departmentName = DEFAULT_NAME;
        department.description = DEFAULT_DESCRIPTION;

        assertFalse(department.isPersistent());
        department = service.insertDepartment(department);
        deptId = department.id;

        assertNotNull(deptId);
        // assertTrue(department.isPersistent());
        assertEquals(DEFAULT_NAME, department.departmentName);
        assertEquals(DEFAULT_DESCRIPTION, department.description);
    }

    @Test
    @Order(5)
    void shouldThrowExceptionOnNullId() {
        assertThrows(ConstraintViolationException.class,
                () -> service.findDepartment(null));
    }

    @Test
    @Order(6)
    void shouldGetDepartment() {
        Optional<Department> department = service.findDepartment(deptId);
        assertTrue(department.isPresent());
        assertEquals(DEFAULT_NAME, department.get().departmentName);
        assertEquals(DEFAULT_DESCRIPTION, department.get().description);
    }

    @Test
    @Order(7)
    void shouldThrowNotFoundExceptionUponUpdate() {
        Department department = new Department();
        department.id = deptId;
        department.departmentName = UPDATED_NAME;
        department.description = UPDATED_DESCRIPTION;

        Long randomId = new Random().nextLong();
        assertThrows(NotFoundException.class, () -> {
            service.updateDepartment(department, randomId);
        });
    }

    @Test
    @Order(8)
    void shouldThrowConstraintsViolationException() {
        Department department = new Department();
        department.id = deptId;
        department.departmentName = null;
        department.description = UPDATED_DESCRIPTION;

        assertThrows(ConstraintViolationException.class,
                () -> service.updateDepartment(department, deptId));
    }

    @Test
    @Order(9)
    void shouldUpdateDepartment() {
        Department department = new Department();
        department.id = deptId;
        department.departmentName = UPDATED_NAME;
        department.description = UPDATED_DESCRIPTION;

        service.updateDepartment(department, deptId);

        department = service.findDepartment(deptId).get();
        assertTrue(department.isPersistent());
        assertEquals(deptId, department.id);
        assertEquals(UPDATED_NAME, department.departmentName);
        assertEquals(UPDATED_DESCRIPTION, department.description);
    }

    @Test
    @Order(10)
    void shouldThrowNotFoundException() {
        Long randomId = new Random().nextLong();
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> service.deleteDepartment(randomId));
        assertEquals(
                "Unable to find com.assets.management.assets.model.entity.Department with id "
                        + randomId,
                thrown.getMessage());
    }

    @Test
    @Order(11)
    void shouldThrowConstraintViolationException() {
        assertThrows(ConstraintViolationException.class,
                () -> service.deleteDepartment(null));
    }

    @Test
    @Order(12)
    void shouldDeleteDepartment() {
        service.deleteDepartment(deptId);
        Optional<Department> department = service.findDepartment(deptId);
        assertFalse(department.isPresent());
    }
}
