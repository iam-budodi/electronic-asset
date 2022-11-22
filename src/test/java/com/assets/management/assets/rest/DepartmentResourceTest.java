package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.Department;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(DepartmentResource.class)
class DepartmentResourceTest {

	private static final String    DEFAULT_NAME = "Technology";
	private static final String    UPDATED_NAME = "Technology (updated)";
	private static final String    DEFAULT_DESCRIPTION = "Technology functions";
	private static final String    UPDATED_DESCRIPTION = "Technology functions (updated)";
	
	private static String departmentId;
	
	@TestHTTPResource
	@TestHTTPEndpoint(DepartmentResource.class)
	URL url;
	
	@Test
	@Order(1)
	void shouldNotFindDepartments() {
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
	void shouldNotCreateInvalidDepartment() {
		final Department department = new Department();
		// department.id = Long.valueOf(1);
		department.name = null;
		department.description = DEFAULT_DESCRIPTION;
	 
			given() 
				.body(department)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(INTERNAL_SERVER_ERROR.getStatusCode());
		 
	}
	
	@Test
	@Order(3)
	void shouldCreateDepartment() {
		final Department department = new Department();
		department.name = DEFAULT_NAME;
		department.description = DEFAULT_DESCRIPTION;
		
		String location =
				given()
				.body(department)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
				.statusCode(Status.CREATED.getStatusCode())
				.header("location", url + "/1")
				.extract().header("location");
		
		assertTrue(location.contains("departments"));
		String[] segments = location.split("/");
		departmentId = segments[segments.length - 1];
		assertNotNull(departmentId);
		assertEquals(String.valueOf(1), departmentId);
	}
	
	@Test
	@Order(4)
	void shouldFindDepartments() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
			 	.statusCode(OK.getStatusCode())
			 	.body("", hasSize(1))
				.contentType(APPLICATION_JSON); 
	}
	
	@Test
	@Order(5)
	void shouldNotGetUnknownDepartment() {
		Long randomId = new Random().nextLong();
		
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.get("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}
	
	@Test
	@Order(6)
	void shouldNotGetDepartmentByUnknownName() {
		String randomName = RandomStringUtils.randomAlphabetic(8);
		
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("name", randomName)
			.when()
			.get("/{name}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}
	
	@Test
	@Order(7)
	void shouldGetDepartmentById() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", departmentId)
			.when()
			.get("/{id}")
			.then()
				.statusCode(OK.getStatusCode())
				.body("id", is(Integer.valueOf(departmentId)))
				.body("name", is(DEFAULT_NAME))
				.body("description", is(DEFAULT_DESCRIPTION));
	}
	
	@Test
	@Order(8)
	void shouldGetDepartmentByName() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.queryParam("name", DEFAULT_NAME)
			.when()
			.get()
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body("id", is(Integer.valueOf(departmentId)))
				.body("name", is(DEFAULT_NAME))
				.body("description", is(DEFAULT_DESCRIPTION));
	}

		
	@Test
	@Order(9)
	void shouldCountDepartment() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body(containsString("1"));
	}

	@Test
	@Order(10)
	void shouldFailToUpdateDepartment() {
		final Department department = new Department();
		department.id = Long.valueOf(departmentId);
		department.name = UPDATED_NAME;
		department.description = UPDATED_DESCRIPTION;
		 
		given()
			.body(department)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", 2)
			.when()
			.put("/{id}")
			.then()
				.statusCode(CONFLICT.getStatusCode());
	}
	
	@Test
	@Order(11)
	void shouldUpdateDepartment() {
		final Department department = new Department();
		department.id = Long.valueOf(departmentId);
		department.name = UPDATED_NAME;
		department.description = UPDATED_DESCRIPTION;
		
		given()
			.body(department)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", departmentId)
			.when()
			.put("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}
 
	@Test
	@Order(12)
	void shouldNotDeleteDepartmentByInvalidId() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", 2)
			.when()
			.delete("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}

	@Test
	@Order(13)
	void shouldDeleteDepartmentById() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", departmentId)
			.when()
			.delete("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}
}
