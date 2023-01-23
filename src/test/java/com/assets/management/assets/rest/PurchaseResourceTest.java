package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.Purchase;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(EmployeeResource.class)
class PurchaseResourceTest {
	private static final LocalDate PURCHASE_DATE = LocalDate.of(2022, Month.JANUARY, 19);
	private static final Integer PURCHASE_QUANTITY = 5;
	private static final BigDecimal PURCHASE_PRICE = BigDecimal.valueOf(1850000).setScale(2, RoundingMode.HALF_UP);
	private static final String INVOICE_NUMBER = "LP-123456CTY";
	
	private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 20);
	private static final Integer UPDATED_PURCHASE_QUANTITY = 6;
	private static final BigDecimal UPDATED_PURCHASE_PRICE = PURCHASE_PRICE;
	private static final String UPDATED_INVOICE_NUMBER = "LP-123456CTY - Updated";
	
//	@TestHTTPResource //("departments")
//	@TestHTTPEndpoint(DepartmentResource.class)
//	static URL deptUrl;
//	
//	@TestHTTPResource("count")
//	@TestHTTPEndpoint(EmployeeResource.class)
//	URL countEndpoint;
	
	@Test
	@Order(1)
	void shouldListEmptyPurchases() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
			 	.statusCode(Status.OK.getStatusCode())
			 	.body("isEmpty()", is(true))
				.contentType(APPLICATION_JSON); 
	}
		
	@Test
	@Order(2)
	void shouldNotFindRandomPurchase() {
		final Long randomId = new Random().nextLong();
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.get("/{id}")
			.then()
			 	.statusCode(NOT_FOUND.getStatusCode())
				.contentType(APPLICATION_JSON); 
	}

	@Test
	@Order(3)
	void shouldNotCreateInvalidPurchase() {
		final Purchase purchase = new Purchase();
			given() 
				.body(purchase)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(BAD_REQUEST.getStatusCode());
	}
	
//	@Test 
//	@Order(4)
//	void ShouldCreateDepartment() {
//		final Department dept = new Department();
//		dept.name = DEPARTMENT_NAME;
//		dept.description = DEPARTMENT_DESCRIPTION;
//		
//		// creates department
//		String deptLocation = given()
//				.body(dept)
//				.header(CONTENT_TYPE, APPLICATION_JSON)
//				.header(ACCEPT, APPLICATION_JSON)
//				.when()
//				.post(deptUrl)
//				.then()
//					.statusCode(Status.CREATED.getStatusCode())
////					.header("location", deptUrl + "/2") // leave for testing failure results
//					.extract().response().getHeader("Location");
//
//		// retrieve created URI and extract department Id
//		String[] elements = deptLocation.split("/");
//		departmentId = elements[elements.length - 1];
//	}
//	
//	@Test 
//	@Order(5) 
//	void shouldFetchDepartment() {
//		// fetch created department 
//		department = given()
//				.accept(ContentType.JSON)
//				.pathParam("id", departmentId)
//				.when()
//				.get(deptUrl + "/{id}")
//				.then()
//					.statusCode(OK.getStatusCode())
//					.contentType(ContentType.JSON)
////					.extract().body().as(getDepartmentTypeRef());
//					.extract().as(Department.class);
////		resetDepartment();
//	}
//	
//	@Test
//	@Order(6)
//	void shouldCreateEmployee() {		
//		// Creating Address object
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = DEFAULT_FNAME;
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = DEFAULT_EMAIL;
//		employee.mobile = DEFAULT_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.address = address;
//		employee.department = department;
//		
//		String location = given()
//				.body(employee)
//				.header(CONTENT_TYPE, APPLICATION_JSON)
//				.header(ACCEPT, APPLICATION_JSON)
//				.when()
//				.post()
//				.then()
//					.statusCode(Status.CREATED.getStatusCode())
//					.extract().response().getHeader("Location");
//		
//		assertTrue(location.contains("employees"));
//		String[] segments = location.split("/");
//		employeeId = segments[segments.length - 1];
//		assertNotNull(employeeId);
//		
//	}
//	
//	@Test
//	@Order(7)
//	void shouldFindEmployees() {
//		List<Employee> employees = given()
//				.header(ACCEPT, APPLICATION_JSON)
//				.when()
//				.get()
//				.then()
//				 	.statusCode(OK.getStatusCode())
//					.body("", is(not(empty())))
//					.contentType(APPLICATION_JSON)
//					.extract().body().as(getEmployeesTypeRef()); 
//		
//		assertTrue(employees.size() >= 1);
//		assertThat(employees.size(), greaterThanOrEqualTo(1));
//	}
//		
//	@Test
//	@Order(8)
//	void shouldFindEmployee() {
//		given()
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", employeeId)
//			.when()
//			.get("/{id}")
//			.then()
//			 	.statusCode(OK.getStatusCode())
//				.contentType(APPLICATION_JSON)
//				.body("firstName", is(DEFAULT_FNAME))
//				.body("lastName", is(DEFAULT_LNAME))
//				.body("email", is(DEFAULT_EMAIL))
//				.body("mobile", is(DEFAULT_PHONE))
//				.body("hireDate", is(DEFAULT_HIRE_DATE.toString()))
//				.body("status", hasItems("CONTRACT", "FULL_TIME"))
//				.body("registeredBy", is(DEFAULT_REGISTERED_BY))
//				.body("department.id", is(Integer.valueOf(departmentId)))
//				.body("address.id", is(notNullValue()))
//				.body("address.street", is(STREET))
//				.body("address.city", is(CITY))
//				.body("address.postalCode", is(POSTAL_CODE));
//	}
//		
//	@Test
//	@Order(9)
//	void shouldCountEmployee() {  
//		given()
//			.header(ACCEPT, TEXT_PLAIN)
//			.when()
//			.get(countEndpoint)
//			.then()
//				.statusCode(OK.getStatusCode())
//				.contentType(TEXT_PLAIN)
////				.body(containsString(String.valueOf(count)))
//				.body(is(greaterThanOrEqualTo(String.valueOf(1))));
//	}
//	
//	@Test
//	@Order(10)
//	void shouldFailToUpdateRandomEmployee() {
//		final Long randomId = new Random().nextLong();
//		
//		final Employee employee = new Employee();
//		employee.id = Long.valueOf(employeeId);
//		employee.firstName = UPDATED_FNAME;
//		employee.lastName = UPDATED_LNAME;
//		employee.email = UPDATED_EMAIL;
//		employee.mobile = UPDATED_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.workId = "UDSM-2023-0012";
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		 
//		given()
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", randomId)
//			.when()
//			.put("/{id}")
//			.then()
//				.statusCode(CONFLICT.getStatusCode());
//	}
//	
//	@Test
//	@Order(11)
//	void shouldFailToUpdateEmployeeWithoutDepartment() {
//		final Long randomId = new Random().nextLong();		
//
//		final Employee employee = new Employee();
//		employee.id = randomId;
//		employee.firstName = UPDATED_FNAME;
//		employee.lastName = UPDATED_LNAME;
//		employee.email = UPDATED_EMAIL;
//		employee.mobile = UPDATED_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.workId = "UDSM-2023-0012";
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		
//		given()
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", randomId)
//			.when()
//			.put("/{id}")
//			.then()
//			    .statusCode(BAD_REQUEST.getStatusCode());
//	}
//	
//	@Test
//	@Order(12)
//	void shouldFailToUpdateUnknownEmployee() {
//		final Long randomId = new Random().nextLong();		
//		
//		final Employee employee = new Employee();
//		employee.id = randomId;
//		employee.firstName = UPDATED_FNAME;
//		employee.lastName = UPDATED_LNAME;
//		employee.email = UPDATED_EMAIL;
//		employee.mobile = UPDATED_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.workId = "UDSM-2023-0012";
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.department = department;
//		
//		given()
//		.body(employee)
//		.header(CONTENT_TYPE, APPLICATION_JSON)
//		.header(ACCEPT, APPLICATION_JSON)
//		.pathParam("id", randomId)
//		.when()
//		.put("/{id}")
//		.then()
//		.statusCode(NOT_FOUND.getStatusCode());
//	}
//		
//	@Test
//	@Order(13)
//	void shouldNotUpdateAddressWhileUpdatingEmployee() {
//		// Creating Address object
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = "15000";
//		address.country = COUNTRY;
//		
//		final Employee employee = new Employee();
//		employee.id = Long.valueOf(employeeId);
//		employee.firstName = UPDATED_FNAME;
//		employee.lastName = UPDATED_LNAME;
//		employee.email = UPDATED_EMAIL;
//		employee.mobile = UPDATED_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.workId = "UDSM-2023-0012";
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.department = department;
//		employee.address = address;
//	 
//		given()
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", employeeId)
//			.when()
//			.put("/{id}")
//			.then()
//				.statusCode(NO_CONTENT.getStatusCode());
//	}
//	
//	@Test
//	@Order(14)
//	void shouldCheckAddressIsNotUpdated() {
//		Employee employee = given()
//				.header(ACCEPT, APPLICATION_JSON)
//				.pathParam("id", employeeId)
//				.when()
//				.get("/{id}")
//				.then()
//				 	.statusCode(OK.getStatusCode())
//					.contentType(APPLICATION_JSON)
//					.extract().body().as(getEmployeeTypeRef()); 
//		
//		assertThat(employee.address.postalCode, not(equalTo("15000")));
//		assertThat(employee.address.postalCode, is(equalTo(POSTAL_CODE)));
//	}
// 
//	@Test
//	@Order(15)
//	void shouldUpdateEmployee() {
//		final Employee employee = new Employee();
//		employee.id = Long.valueOf(employeeId);
//		employee.firstName = UPDATED_FNAME;
//		employee.lastName = UPDATED_LNAME;
//		employee.email = DEFAULT_EMAIL;
//		employee.mobile = "+(255) 744 608 510";
//		employee.gender = DEFAULT_GENDER;
//		employee.workId = "UDSM-2023-0002";
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.department = department;
//	 
//		given()
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", employeeId)
//			.when()
//			.put("/{id}")
//			.then()
//				.statusCode(NO_CONTENT.getStatusCode());
//	}
//	
//	@Test
//	@Order(16)
//	void shouldNotDeleteUnknownEmployee() { 
//		Long randomId = new Random().nextLong(); 
//		given()
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", randomId)
//			.when()
//			.delete("/{id}")
//			.then()
//				.statusCode(NOT_FOUND.getStatusCode());
//	}
//	
//	@Test
//	@Order(17)
//	void shouldDeleteEmployee() {  
//		given()
//			.header(ACCEPT, APPLICATION_JSON)
//			.pathParam("id", employeeId)
//			.when()
//			.delete("/{id}")
//			.then()
//				.statusCode(NO_CONTENT.getStatusCode());
//	}
//
//	@Test
//	@Order(18)
//	void shouldFailDeleteUnexistingEmployeeFromDepartment() {		
//		// delete associated employee from employees table
//		given()
//			.accept(ContentType.JSON)
//			.pathParam("id", departmentId)
//			.queryParam("empid", employeeId)
//			.when()
//			.delete(deptUrl + "/{id}/employee")
//			.then()
//				.statusCode(NOT_FOUND.getStatusCode());
//	}
//	
//	@Test
//	@Order(19)
//	void shouldResetDepartment() {		
//		// reset department table
//		given()
//			.accept(ContentType.JSON)
//			.pathParam("id", departmentId)
//			.when()
//			.delete(deptUrl + "/{id}")
//			.then()
//				.statusCode(NO_CONTENT.getStatusCode());
//	}
//
//	@Test
//	@Order(20)
//	void shouldNotCreateEmployeeWhenSupplyingNullValuesForRequiredFields() {
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = DEFAULT_FNAME;
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = null;
//		employee.mobile = DEFAULT_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.address = null;
//		employee.department = department;
//		
//		given() 
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.when()
//			.post()
//			.then()
//				.statusCode(BAD_REQUEST.getStatusCode());
//
//	}
//
//	@Test
//	@Order(21)
//	void shouldNotCreateEmployeeWhenInvalidCharatersSuppliedOnNamesFields() {
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = "J@ph3t";
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = DEFAULT_EMAIL;
//		employee.mobile = DEFAULT_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.address = address;
//		employee.department = department;
//		
//		given() 
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.when()
//			.post()
//			.then()
//				.statusCode(BAD_REQUEST.getStatusCode());
//
//	}
//
//	@Test
//	@Order(22)
//	void shouldNotCreateEmployeeWhenInvalidCharateruppliedOnEmailField() {
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = DEFAULT_FNAME;
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = "j@phetseba@gmail.com";
//		employee.mobile = DEFAULT_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.address = address;
//		employee.department = department;
//		
//		given() 
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.when()
//			.post()
//			.then()
//				.statusCode(BAD_REQUEST.getStatusCode());
//
//	}
//
//	@Test
//	@Order(23)
//	void shouldNotCreateEmployeeWhenInvalidCharaterSuppliedOnMobileNumberField() {
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = DEFAULT_FNAME;
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = DEFAULT_EMAIL;
//		employee.mobile = "(255)744.111.789";
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.address = address;
//		employee.department = department;
//		
//		given() 
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.when()
//			.post()
//			.then()
//				.statusCode(BAD_REQUEST.getStatusCode());
//
//	}
//	
//	@Test
//	@Order(24)
//	void shouldNotCreateEmployeeWithoutAddress() {
//		final Address address = new Address();
//		address.street = STREET;
//		address.ward = WARD;
//		address.district = DISTRICT;
//		address.city = CITY;
//		address.postalCode = POSTAL_CODE;
//		address.country = COUNTRY;
//
//		final Employee employee = new Employee();
//		employee.firstName = DEFAULT_FNAME;
//		employee.lastName = DEFAULT_LNAME;
//		employee.email = DEFAULT_EMAIL;
//		employee.mobile = DEFAULT_PHONE;
//		employee.gender = DEFAULT_GENDER;
//		employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//		employee.hireDate = DEFAULT_HIRE_DATE;
//		employee.status = DEFAULT_STATUS;
//		employee.registeredBy = DEFAULT_REGISTERED_BY;
//		employee.department = department;
//		
//		given() 
//			.body(employee)
//			.header(CONTENT_TYPE, APPLICATION_JSON)
//			.header(ACCEPT, APPLICATION_JSON)
//			.when()
//			.post()
//			.then()
//				.statusCode(BAD_REQUEST.getStatusCode());
//	}
//
//	private TypeRef<List<Employee>> getEmployeesTypeRef() {
//		return new TypeRef<List<Employee>>() {
//		};
//	}
//
//	private TypeRef<Employee> getEmployeeTypeRef() {
//		return new TypeRef<Employee>() {
//		};
//	}

}
