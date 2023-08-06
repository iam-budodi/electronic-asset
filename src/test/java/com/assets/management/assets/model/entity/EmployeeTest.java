package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.EmploymentStatus;
import com.assets.management.assets.model.valueobject.Gender;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeTest {
    private static final String DEFAULT_FNAME = "Lulu";
    private static final String DEFAULT_LNAME = "Shaban";
    private static final String DEFAULT_EMAIL = "luluyshaban@gmail.com";
    private static final String DEFAULT_PHONE = "0716 656 596";
    private static final Gender DEFAULT_GENDER = Gender.F;
    private static final String DEFAULT_WORK_ID = "UDSM-2013-00001";
    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(1997, Month.SEPTEMBER, 12);
    private static final LocalDate DEFAULT_HIRE_DATE = LocalDate.of(2022, Month.JUNE, 04);
    private static final Set<EmploymentStatus> DEFAULT_STATUS = EnumSet.of(EmploymentStatus.CONTRACT,
            EmploymentStatus.PROBATION);
    private static final String DEFAULT_REGISTERED_BY = "Japhet";

    private static final String UPDATED_FNAME = "Lulu - updated";
    private static final String UPDATED_LNAME = "Shaban - updated";
    private static final String UPDATED_EMAIL = "lulu.shaban@gmail.com";
    private static final String UPDATED_PHONE = "0716656596";

    private static final String STREET = "Mikocheni";
    private static final String WARD = "Msasani";
    private static final String DISTRICT = "Kinondoni";
    private static final String CITY = "Dar es Salaam";
    private static final String POSTAL_CODE = "14110";
    private static final String COUNTRY = "Tanzania";
//
//	private static final String DEPARTMENT_NAME = "Finance";
//	private static final String DEPARTMENT_DESCRIPTION = "Finance department";

    private static Long employeeId;
//	private static Long deptId;

    @Test
    @Order(1)
    void shouldPersistEmployee() {
        final Address address = new Address();
        address.street = STREET;
        address.ward = WARD;
        address.district = DISTRICT;
        address.city = CITY;
        address.postalCode = POSTAL_CODE;
        address.country = COUNTRY;

//		final Department department = new Department();
//		department.name = DEPARTMENT_NAME;
//		department.description = DEPARTMENT_DESCRIPTION;

        final Employee employee = new Employee();
        employee.firstName = DEFAULT_FNAME;
        employee.lastName = DEFAULT_LNAME;
        employee.email = DEFAULT_EMAIL;
        employee.mobile = DEFAULT_PHONE;
        employee.gender = DEFAULT_GENDER;
        employee.workId = DEFAULT_WORK_ID;
        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
        employee.hireDate = DEFAULT_HIRE_DATE;
        employee.status = DEFAULT_STATUS;
        employee.registeredBy = DEFAULT_REGISTERED_BY;
        employee.address = address;
//		employee.department = department;

//		Supplier.persist(department, employee);
        Supplier.persist(employee);

        assertTrue(employee.isPersistent());
        assertNotNull(employee.id);
//		assertNotNull(department.id);

        employeeId = employee.id;
//		deptId = department.id;
    }

    @Test
    @Order(2)
    void shouldlistAll() {
        List<Employee> employees = Employee.listAll(Sort.by("firstName").and("lastName", Direction.Descending));
        assertTrue(employees.size() >= 1);
    }

    @Test
    @Order(3)
    void shouldFindSpecificEmployee() {
        Employee employee = Employee.findById(employeeId);
        assertThat(employee)
                .extracting("email", as(InstanceOfAssertFactories.STRING))
                .contains(DEFAULT_EMAIL)
                .isNotEmpty()
                .isNotNull();
    }

    @Test
    @Order(4)
    void shouldFindOptionalSpecificEmployee() {
        Optional<Employee> optional = Employee.findByIdOptional(employeeId);
        assertThat(optional)
                .isNotEmpty()
                .map(employee -> employee.email)
                .contains(DEFAULT_EMAIL);
    }

    @Test
    @Order(5)
    void shouldFindEmployeeUsingEntityManager() {
        Employee employee = Employee.getEntityManager().find(Employee.class, employeeId);
        assertNotNull(employee);
    }

    @Test
    @Order(6)
    void shouldCountAll() {
        Long total = Employee.count();
        assertTrue(total >= 1);
    }

    @Test
    @Order(7)
    void shouldUpdate() {
        final Employee employee = new Employee();
        employee.id = employeeId;
        employee.firstName = UPDATED_FNAME;
        employee.lastName = UPDATED_LNAME;
        employee.email = UPDATED_EMAIL;
        employee.mobile = UPDATED_PHONE;
        employee.gender = DEFAULT_GENDER;
        employee.workId = DEFAULT_WORK_ID;
        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
        employee.hireDate = DEFAULT_HIRE_DATE;
        employee.status = DEFAULT_STATUS;
        employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.department = Department.findById(deptId);

        Panache.getEntityManager().merge(employee); // this is not necessary can be commented out
        Assertions.assertFalse(DEFAULT_FNAME.equals(employee.firstName));
        assertTrue(UPDATED_FNAME.equals(employee.firstName));
    }

    @Test
    @Order(8)
    void shouldDelete() {
        Employee employee = Employee.findById(employeeId);
        if (employee.isPersistent())
            employee.delete();
    }

    @Test
    @Order(9)
    void shouldConfirmDeleted() {
        boolean deleted = Employee.deleteById(employeeId);
        Assertions.assertFalse(deleted);
    }

    @Test
    @Order(10)
    void shouldCheckThereIsNothing() {
        Assertions.assertFalse(Employee.checkByEmailAndPhone(UPDATED_EMAIL, UPDATED_PHONE));
    }
}
