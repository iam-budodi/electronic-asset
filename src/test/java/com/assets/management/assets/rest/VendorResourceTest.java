package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.Vendor;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VendorResourceTest { 
	
	// end user
	private static final String    DEFAULT_COMPANY_NAME = "TA";
	private static final String    UPDATED_COMPANY_NAME = "TA (updated)";
	private static final String    DEFAULT_SERVICE = "Office Computer";
	private static final String    UPDATED_SERVICE  = "Office Computer (updated)";
	private static final String    DEFAULT_CONTACT_PERSON   = "Richard Seba";
	private static final String    UPDATED_CONTACT_PERSON    = "Jangid Plaza, 3rd Floor (updated)";
	private static final String    DEFAULT_ESCALATING_PERSON     = "Japhet Sebastian";
	private static final String    UPDATED_ESCALATING_PERSON      = "Japhet Sebastian (updated)";

	private static String vendorId;  
	
	@TestHTTPResource
	@TestHTTPEndpoint(VendorResource.class)
	URL url;
	
	@Test
	@Order(1)
	void shouldNotAddInvalidVendor() {
		Vendor vendor = new Vendor();
		vendor.companyName = null; 

		given()
			.body(vendor)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT,  APPLICATION_JSON).
		when()
			.post("/rest/vendors").
		then()
			.statusCode(BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(2)
	void shouldGetNoVendor() {
		given()
			.header(ACCEPT, APPLICATION_JSON).
		when()
			.get( "/rest/users").
		then()
			.statusCode(OK.getStatusCode()) 
			.body("isEmpty()", is(true));
	}
	
	@Test
	@Order(3)
	void shouldAddVendor() {
		Vendor vendor = new Vendor();
		vendor.companyName = DEFAULT_COMPANY_NAME;
		vendor.service = DEFAULT_SERVICE;
		vendor.contactPerson = DEFAULT_CONTACT_PERSON;
		vendor.escalatingPerson = DEFAULT_ESCALATING_PERSON;
		
		String location = 
			given()
				.body(vendor)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT,  APPLICATION_JSON).
			when()
				.post("/rest/vendors").
			then()
				.statusCode(CREATED.getStatusCode()) 
				.header(LOCATION, "http://localhost:8081/rest/vendors/1") 
				.extract().header("Location"); 
		
		// Extracts the location and stores asset user id 
		assertTrue(location.contains("/rest/vendors"));
		String[] segments = location.split("/");
		vendorId = segments[segments.length - 1];
		assertNotNull(vendorId);
	}
	 
	@Test
	@Order(4)
	void shouldGetVendors() {
		given()
			.accept(APPLICATION_JSON).
		when()
			.get( "/rest/vendors").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(APPLICATION_JSON) 
			.body("", hasSize(1));
	}
	
	@Test
	@Order(5)
	void shouldCountVendor() {
		given()
			.accept(APPLICATION_JSON).
		when()
			.get( "/rest/vendors/count").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(APPLICATION_JSON)
			.body(is(String.valueOf(1)));
	}
		
	@Test
	@Order(6)
	void shouldCheckCreatedVendor() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", vendorId).
		when()
			.get("/rest/vendors/{id}").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(ContentType.JSON) 
			.body("companyName", is(DEFAULT_COMPANY_NAME))
			.body("service", is(DEFAULT_SERVICE))
			.body("contactPerson", is(DEFAULT_CONTACT_PERSON))
			.body("escalatingPerson", is(DEFAULT_ESCALATING_PERSON));
	}
	
	@Test
	@Order(7)
	void shouldUpdateVendor() {
		Vendor vendor = new Vendor();
		vendor.id = Long.valueOf(vendorId);
		vendor.companyName = UPDATED_COMPANY_NAME;
		vendor.service = UPDATED_SERVICE;
		vendor.contactPerson = UPDATED_CONTACT_PERSON;
		vendor.escalatingPerson = UPDATED_ESCALATING_PERSON;
		
		given()
			.contentType(APPLICATION_JSON)
			.pathParam("id", vendorId)
			.body(vendor)
		.when()
			.put("/rest/vendors/{id}")
		.then()
			.statusCode(NO_CONTENT.getStatusCode());
	}
	
	@Test
	@Order(8)
	void shouldCheckUpdatedVendor() {
		List<Vendor> vendors =
			given()
				.accept(APPLICATION_JSON).
			when()
				.get( "/rest/vendors").
			then()
				.statusCode(OK.getStatusCode()).
			and()
				.contentType(APPLICATION_JSON)
				.extract().body().as(getVendorTypeRef()); 
		
		assertEquals(Integer.valueOf(1), vendors.size());
		assertNotEquals(DEFAULT_COMPANY_NAME, vendors.get(0).companyName);
	}
		
	@Test
	@Order(9)
	void shouldRemoveAllVendors() {
		given()
			.accept(APPLICATION_JSON)
			.contentType(APPLICATION_JSON).
		when()
			.delete("rest/vendors").
		then()
			.statusCode(OK.getStatusCode()).
		and()
			.contentType(ContentType.JSON) 
			.body(is(String.valueOf(1)));
	}
	 
	@Test
	@Order(10)
	void shouldNotFindVendor() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", vendorId).
		when()
			.get("/rest/vendors/{id}").
		then()
			.statusCode(NOT_FOUND.getStatusCode());
	}
	 
	@Test
	@Order(11)
	void shouldFailToRemoveUnexistingVendor() {
		given()
			.accept(ContentType.JSON)
			.pathParam("id", vendorId).
		when()
			.delete("/rest/vendors/{id}").
		then()
			.statusCode(NOT_FOUND.getStatusCode());
	}
	
	// TODO: Tests add assets
	
	private TypeRef<List<Vendor>> getVendorTypeRef() {
		return new TypeRef<List<Vendor>>() {
			
		};
	} 
}
