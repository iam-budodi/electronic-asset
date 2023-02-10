package com.assets.management.assets.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.entity.Address;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.valueobject.EmploymentStatus;
import com.assets.management.assets.model.valueobject.Gender;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeServiceTest {
	private static final String DEFAULT_FNAME = "Japhet";
	private static final String DEFAULT_LNAME = "Sebastian";
	private static final String DEFAULT_EMAIL = "japhetseba@gmail.com";
	private static final String DEFAULT_PHONE = "(255) 744 608 510";
	private static final Gender DEFAULT_GENDER = Gender.MALE;
	private static final String DEFAULT_WORK_ID = "UDSM-2013-00002";
	private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(1992, Month.JUNE, 12);
	private static final LocalDate DEFAULT_HIRE_DATE = LocalDate.of(2019, Month.APRIL, 8);
	private static final Set<EmploymentStatus> DEFAULT_STATUS = EnumSet.of(EmploymentStatus.CONTRACT,
			EmploymentStatus.FULL_TIME);
	private static final String DEFAULT_REGISTERED_BY = "Lulu";

	private static final String UPDATED_FNAME = "Japhet - updated";
	private static final String UPDATED_LNAME = "Sebastian - updated";
	private static final String UPDATED_EMAIL = "luluyshaban@gmail.com";
	private static final String UPDATED_PHONE = "0716656596";

	private static final String STREET = "Mikocheni";
	private static final String WARD = "Msasani";
	private static final String DISTRICT = "Kinondoni";
	private static final String CITY = "Dar es Salaam";
	private static final String POSTAL_CODE = "14110";
	private static final String COUNTRY = "Tanzania";

	private static final Integer PAGE_INDEX = 0;
	private static final Integer PAGE_SIZE = 15;

	private static Long employeeId;
	private static int nbEmployees;

	@Inject
	EmployeeService employeeService;

	@Test
	@Order(1)
	void shouldFetchInitialEmployeesState() {
		nbEmployees = employeeService.listEmployees(PAGE_INDEX, PAGE_SIZE).size();
		assertEquals(nbEmployees, Employee.count());
		Assert.assertTrue(nbEmployees >= 0);
	}

	@Test
	@Order(2)
	void shouldAddEmployee() {
		final Address address = new Address();
		address.street = STREET;
		address.ward = WARD;
		address.district = DISTRICT;
		address.city = CITY;
		address.postalCode = POSTAL_CODE;
		address.country = COUNTRY;

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

		assertFalse(employee.isPersistent());
		employeeService.addEmployee(employee);
		employeeId = employee.id;

		assertNotNull(employeeId);
		assertNotNull(employee.address);
		assertNotNull(employee.workId);
		assertEquals(DEFAULT_FNAME, employee.firstName);
		assertEquals(DEFAULT_LNAME, employee.lastName);
		assertEquals(DEFAULT_EMAIL, employee.email);
		assertEquals(DEFAULT_PHONE, employee.mobile);
		assertEquals(DEFAULT_GENDER, employee.gender);
		assertEquals(DEFAULT_BIRTH_DATE, employee.dateOfBirth);
		assertEquals(DEFAULT_HIRE_DATE, employee.hireDate);
		assertEquals(DEFAULT_STATUS, employee.status);
		assertEquals(DEFAULT_REGISTERED_BY, employee.registeredBy);
	}

	@Test
	@Order(3)
	void shouldCheckForExtraEmployee() {
		assertEquals(nbEmployees + 1, employeeService.listEmployees(PAGE_INDEX, PAGE_SIZE).size());
	}

	@Test
	@Order(4)
	void shouldUpdate() {
		final Employee employee = new Employee();
		employee.id = employeeId;
		employee.firstName = UPDATED_FNAME;
		employee.lastName = UPDATED_LNAME;
		employee.email = UPDATED_EMAIL;
		employee.mobile = UPDATED_PHONE;
		employee.gender = DEFAULT_GENDER;
		employee.workId = "UDSM-2023-0012";
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;

		employeeService.updateEmployee(employee, employeeId);
		Employee found = Employee.findById(employeeId);
		assertFalse(DEFAULT_FNAME.equals(employee.firstName));
		assertTrue(UPDATED_FNAME.equals(employee.firstName));
		assertEquals("UDSM-2023-0012", found.workId);
	}

	@Test
	@Order(5)
	void shouldDeleteSupplier() {
		employeeService.deleteEmployee(employeeId);
		assertFalse(Employee.checkByEmailAndPhone(UPDATED_EMAIL, UPDATED_PHONE));
	}

	@Test
	@Order(6)
	void shouldThrowExceptionOnInsertingNullEmployeeObject() {
		Employee employee = new Employee();
		ConstraintViolationException thrown = assertThrows(
				ConstraintViolationException.class, 
				() -> employeeService.addEmployee(employee));
		assertEquals(null, thrown.getCause());
	}

	@Test
	@Order(7)
	void shouldThrowExceptionOnSupplyingNullValuesForRequiredFields() {
		final Address address = new Address();
		address.street = STREET;
		address.ward = WARD;
		address.district = DISTRICT;
		address.city = CITY;
		address.postalCode = POSTAL_CODE;
		address.country = COUNTRY;

		final Employee employee = new Employee();
		employee.firstName = DEFAULT_FNAME;
		employee.lastName = DEFAULT_LNAME;
		employee.email = null;
		employee.mobile = DEFAULT_PHONE;
		employee.gender = DEFAULT_GENDER;
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;
		employee.address = address;

		ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class,
				() -> employeeService.addEmployee(employee));
		assertEquals(null, thrown.getCause());
	}

	@Test
	@Order(8)
	void shouldThrowExceptionFetchingSupplierByNullId() {
		assertThrows(NullPointerException.class, 
				() -> employeeService.listEmployees(null, null));
	}

	@Test
	@Order(9)
	void shouldThrowNotFoundExceptionUponUpdate() {
		final Long randomId = new Random().nextLong();
		final Employee employee = new Employee();
		employee.id = employeeId;
		employee.firstName = UPDATED_FNAME;
		employee.lastName = UPDATED_LNAME;
		employee.email = UPDATED_EMAIL;
		employee.mobile = UPDATED_PHONE;
		employee.gender = DEFAULT_GENDER;
		employee.workId = "UDSM-2023-0012";
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;

		assertThrows(NotFoundException.class,
				() -> employeeService.updateEmployee(employee, randomId));
	}

	@Test
	@Order(10)
	void shouldThrowNotFoundExceptionForInvalidId() {
		final Long randomId = new Random().nextLong();
		EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
				() -> employeeService.deleteEmployee(randomId));
		assertEquals(
				"Unable to find com.assets.management.assets.model.entity.Employee with id " + randomId,
				thrown.getMessage());
	}

	@Test
	@Order(11)
	void shouldThrowConstraintViolationExceptionForNullId() {
		assertThrows(ConstraintViolationException.class, 
				() -> employeeService.deleteEmployee(null));
	}
	
	@Test
	@Order(12)
	void shouldThrowExceptionInvalidCharaterOnNamesWhenCreateSupplier() {
		final Address address = new Address();
		address.street = STREET;
		address.ward = WARD;
		address.district = DISTRICT;
		address.city = CITY;
		address.postalCode = POSTAL_CODE;
		address.country = COUNTRY;

		final Employee employee = new Employee();
		employee.firstName = "Japhet$";
		employee.lastName = DEFAULT_LNAME;
		employee.email = DEFAULT_EMAIL;
		employee.mobile = DEFAULT_PHONE;
		employee.gender = DEFAULT_GENDER;
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;
		employee.address = address;
		
		assertThrows(ConstraintViolationException.class, 
				() -> employeeService.addEmployee(employee));
	}
	
	@Test
	@Order(13)
	void shouldThrowExceptionInvalidCharaterOnEmailWhenCreateSupplier() {
		final Address address = new Address();
		address.street = STREET;
		address.ward = WARD;
		address.district = DISTRICT;
		address.city = CITY;
		address.postalCode = POSTAL_CODE;
		address.country = COUNTRY;

		final Employee employee = new Employee();
		employee.firstName =DEFAULT_FNAME;
		employee.lastName = DEFAULT_LNAME;
		employee.email = "japhetseba*@gmail.com";
		employee.mobile = DEFAULT_PHONE;
		employee.gender = DEFAULT_GENDER;
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;
		employee.address = address;
		
		assertThrows(ConstraintViolationException.class, 
				() -> employeeService.addEmployee(employee));
	}
	
	@Test
	@Order(14)
	void shouldThrowExceptionOnInvalidCharaterOnMobileNumberWhenCreateSupplier() {
		final Address address = new Address();
		address.street = STREET;
		address.ward = WARD;
		address.district = DISTRICT;
		address.city = CITY;
		address.postalCode = POSTAL_CODE;
		address.country = COUNTRY;

		final Employee employee = new Employee();
		employee.firstName =DEFAULT_FNAME;
		employee.lastName = DEFAULT_LNAME;
		employee.email = DEFAULT_EMAIL;
		employee.mobile = "(255)744.111.789";
		employee.gender = DEFAULT_GENDER;
		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
		employee.hireDate = DEFAULT_HIRE_DATE;
		employee.status = DEFAULT_STATUS;
		employee.registeredBy = DEFAULT_REGISTERED_BY;
		employee.address = address;
		
		assertThrows(ConstraintViolationException.class, 
				() -> employeeService.addEmployee(employee));
	}
}
