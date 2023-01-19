package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class DepartmentResourceTestIT extends DepartmentResourceTest {
	@Test
	@Override
	void shouldOrShouldNotFindDepartments() {	
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
			 	.statusCode(Status.OK.getStatusCode())
				.contentType(APPLICATION_JSON); 
	}
}
