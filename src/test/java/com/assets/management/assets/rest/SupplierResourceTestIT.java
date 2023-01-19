package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.not;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.net.URL;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class SupplierResourceTestIT extends SupplierResourceTest {
	
	@TestHTTPResource("count")
	@TestHTTPEndpoint(SupplierResource.class)
	URL countEndpoint;
	
	@Test
	@Override
	void shouldFindSuppliers() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
			 	.statusCode(OK.getStatusCode())
				.body("", is(not(empty())))
				.contentType(APPLICATION_JSON);
	}
	
	@Test
	@Override
	void shouldCountSupplier() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get(countEndpoint)
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body(is(greaterThanOrEqualTo(String.valueOf(1))));
	}
}
