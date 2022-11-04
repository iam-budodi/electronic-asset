package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.EndUser;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestHTTPEndpoint(EndUserResource.class)
class EndUserResourceTest { 
	
	// end user
	private static final String    DEFAULT_FIRST_NAME = "Japhet";
	private static final String    UPDATED_FIRST_NAME = "Japhet (updated)";
	private static final String    DEFAULT_LAST_NAME  = "Sebastian";
	private static final String    UPDATED_LAST_NAME  = "Sebastian (updated)";
	private static final String    DEFAULT_ADDRESS    = "Jangid Plaza, 3rd Floor";
	private static final String    UPDATED_ADDRESS    = "Jangid Plaza, 3rd Floor (updated)";
	private static final String    DEFAULT_EMAIL      = "japhetseba@gmail.com";
	private static final String    UPDATED_EMAIL      = "japhetseba@gmail.com (updated)";
	private static final String    DEFAULT_PHONE      = "0744608510";
	private static final String    UPDATED_PHONE      = "255744608510";
	private static final LocalDate DEFAULT_BIRTHDATE  = LocalDate.of(
	        1992, 06, 23
	);
	private static final LocalDate UPDATED_BIRTHDATE  = LocalDate.of(
	        1992, 06, 26
	);

//	// asset for end user
//	private static final String    DEFAULT_MODEL_NAME = "Macbook Pro 2022 M2 Chip";
//	private static final String    DEFAULT_MODEL_NUMBER = "MB-P2022M/CP";
//	private static final String    DEFAULT_SERIAL_NUMBER  = "R58R52PG3CH";
	
	private static String candidateId; 
	
//	@TestHTTPResource
//	@TestHTTPEndpoint(EndUserResource.class)
//	URL url;
	
	@Test
	@Order(1)
	void shouldNotAddInvalidAssetUser() {
		EndUser assetUser = new EndUser();
		assetUser.firstName = null; 

		given()
			.body(assetUser)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT,  APPLICATION_JSON).
		when()
			.post("/rest/users").
		then()
			.statusCode(BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(2)
	void shouldGetNoAssetUser() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get( "/rest/users")
			.then()
			.statusCode(OK.getStatusCode())
			.body("", equalTo(Collections.emptyList()));
//		.body("isEmpty()", Matchers.is(true));
	}
	
	@Test
	@Order(3)
	void shouldAddAssetUser() {
		EndUser assetUser = new EndUser();
		assetUser.firstName = DEFAULT_FIRST_NAME;
		assetUser.lastName = DEFAULT_LAST_NAME;
		assetUser.address = DEFAULT_ADDRESS;
		assetUser.email = DEFAULT_EMAIL;
		assetUser.phone = DEFAULT_PHONE;
		assetUser.birthDate = DEFAULT_BIRTHDATE;
		
		String location = 
			given()
				.body(assetUser)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT,  APPLICATION_JSON)
				.when()
				.post("/rest/users")
				.then()
				.statusCode(CREATED.getStatusCode())
				.extract()
				.header("Location"); 
		
		// Extracts the location and stores asset user id 
		assertTrue(location.contains("/rest/users"));
		String[] segments = location.split("/");
		candidateId = segments[segments.length - 1];
		assertNotNull(candidateId);
	}
	 
	@Test
	@Order(4)
	void shouldGetAssetUser() {
		given()
			.accept(APPLICATION_JSON).
		when()
			.get( "/rest/users").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(APPLICATION_JSON)
			.body("size()", is(1));
//		.body("", hasSize(1));
	}
	
	@Test
	@Order(5)
	void shouldCountAssetUser() {
		given()
			.accept(APPLICATION_JSON).
		when()
			.get( "/rest/users/count").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(APPLICATION_JSON)
			.body(is(String.valueOf(1)));
	}
	
	@Test
	@Order(6)
	void shouldCheckCreatedCandidate() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", candidateId).
		when()
			.get("/rest/users/{id}").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(ContentType.JSON) 
			.body("firstName", is(DEFAULT_FIRST_NAME))
			.body("lastName", is(DEFAULT_LAST_NAME))
			.body("address", is(DEFAULT_ADDRESS))
			.body("email", is(DEFAULT_EMAIL))
			.body("phone", is(DEFAULT_PHONE))
			.body("birthDate", is(DEFAULT_BIRTHDATE.toString()));
	}
	
	@Test
	@Order(7)
	void shouldUpdateAssetUser() {
		EndUser assetUser = new EndUser();
		assetUser.id = Long.valueOf(candidateId);
		assetUser.firstName = UPDATED_FIRST_NAME;
		assetUser.lastName = UPDATED_LAST_NAME;
		assetUser.address = UPDATED_ADDRESS;
		assetUser.email = UPDATED_EMAIL;
		assetUser.phone = UPDATED_PHONE;
		assetUser.birthDate = UPDATED_BIRTHDATE; 
		
		given()
			.contentType(APPLICATION_JSON)
			.pathParam("id", candidateId)
			.body(assetUser)
		.when()
			.put("/rest/users/{id}")
		.then()
			.statusCode(NO_CONTENT.getStatusCode()); 
	}
	
	@Test
	@Order(8)
	void shouldCheckUpdatedAssetUser() {
		List<EndUser> users =
			given()
				.accept(APPLICATION_JSON).
			when()
				.get( "/rest/users").
			then()
				.statusCode(OK.getStatusCode()).
			and()
				.contentType(APPLICATION_JSON)
				.extract().body().as(getEndUserTypeRef()); 
		
		assertEquals(Integer.valueOf(1), users.size());
		assertNotEquals(DEFAULT_FIRST_NAME, users.get(0).firstName);
	}
	
	@Test
	@Order(9)
	void shouldRemoveAllEndUser() {
		given()
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON).
		when()
			.delete("rest/users").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(ContentType.JSON) 
			.body(is(String.valueOf(1)));
	}
	 
	@Test
	@Order(10)
	void shouldNotFindEndUser() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", candidateId).
		when()
			.get("/rest/users/{id}").
		then()
			.statusCode(NOT_FOUND.getStatusCode());
	}
	
	@Test
	@Order(11)
	void shouldFailToRemoveUnexistingEndUser() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", candidateId).
		when()
			.delete("/rest/users/{id}").
		then()
			.statusCode(NOT_FOUND.getStatusCode());
	}
//	
//	@Test
//	@Order(12)
//	void shouldAssignAssetToEndUser() {
//		PanacheMock.mock(Vendor.class);
//		PanacheMock.mock(Asset.class);
//		
//		Vendor vendor = new Vendor("UBX", "Data Centre", "Richard Seba", "Japhet Sebastian");
//		Asset asset = new Asset("Server rack", "XYZ12345", "R58R52PG3CH");
//		
//		Mockito.
//	}
//	
	private TypeRef<List<EndUser>> getEndUserTypeRef() {
		return new TypeRef<List<EndUser>>() {
			
		};
	}
}
