package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Address;
import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.SupplierType;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.ws.rs.core.Response.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URL;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(SupplierResource.class)
class SupplierResourceTest {
    private static final String DEFAULT_NAME = "Technology Associate";
    private static final String DEFAULT_EMAIL = "technology.associate@technologyassociate.co.tz";
    private static final String DEFAULT_PHONE = "0744111789";
    private static final String DEFAULT_REGISTERED_BY = "Japhet";
    private static final String DEFAULT_DESCRIPTION = "Technology associates";
    private static final String UPDATED_NAME = "Technology Associate -  updated";
    private static final String UPDATED_EMAIL = "technology.associate@technologyassociate-inc.com";
    private static final String UPDATED_PHONE = "+(255)-744-111-789";
    private static final String UPDATED_REGISTERED_BY = "Japhet - updated";
    private static final String UPDATED_DESCRIPTION = "Technology associates (updated)";

    private static final String DEFAULT_STREET = "Mikocheni";
    private static final String DEFAULT_WARD = "Msasani";
    private static final String DEFAULT_DISTRICT = "Kinondoni";
    private static final String DEFAULT_CITY = "Dar es Salaam";
    private static final String DEFAULT_POSTAL_CODE = "14110";
    private static final String DEFAULT_COUNTRY = "Tanzania";

    public static String supplierId;
    public static Long addressId;

    @TestHTTPResource("count")
    @TestHTTPEndpoint(SupplierResource.class)
    URL countEndpoint;

    @Test
    @Order(1)
    void shouldFetchEmptySupplierList() {
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .get()
                .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(2)
    void shouldNotFindSupplier() {
        Long randomId = new Random().nextLong();
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
    void shouldNotCreateInvalidSupplier() {
        final Supplier supplier = new Supplier();
        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(4)
    void shouldCreateSupplier() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        final Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.address = address;

        String location = given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertTrue(location.contains("suppliers"));
        String[] segments = location.split("/");
        supplierId = segments[segments.length - 1];
        assertNotNull(supplierId);
    }

    @Test
    @Order(5)
    void shouldFindSuppliers() {
        List<Supplier> suppliers = given()
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .body("", hasSize(greaterThanOrEqualTo(1)))
                .contentType(APPLICATION_JSON)
                .extract().body().as(getSuppliersTypeRef());

        addressId = suppliers.get(0).address.id;
    }

    @Test
    @Order(6)
    void shouldFindSupplier() {
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", supplierId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body("name", is(DEFAULT_NAME))
                .body("email", is(DEFAULT_EMAIL))
                .body("phone", is(DEFAULT_PHONE))
                .body("supplierType", is(SupplierType.RETAILER.toString()))
                .body("description", is(DEFAULT_DESCRIPTION))
                .body("registeredBy", is(DEFAULT_REGISTERED_BY));
    }

    @Test
    @Order(7)
    void shouldNotGetSupplierByUnknownParameter() {
        final Long randomId = new Random().nextLong();
        final String randomParam = RandomStringUtils.randomAlphabetic(6);
        String pathParam = "/{" + randomParam + "}";
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam(randomParam, randomId)
                .when()
                .get(pathParam)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(8)
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

    @Test
    @Order(9)
    void shouldFailToUpdateRandomSupplier() {
        final Long randomId = new Random().nextLong();
        final Supplier supplier = new Supplier();
        supplier.id = Long.valueOf(supplierId);
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESALER;
        supplier.description = UPDATED_DESCRIPTION;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", randomId)
                .when()
                .put("/{id}")
                .then()
                .statusCode(CONFLICT.getStatusCode());
    }

    @Test
    @Order(10)
    void shouldFailToUpdateUnknownSupplier() {
        final Long randomId = new Random().nextLong();
        final Supplier supplier = new Supplier();
        supplier.id = randomId;
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESALER;
        supplier.description = UPDATED_DESCRIPTION;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", randomId)
                .when()
                .put("/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(11)
    void shouldNotUpdateAddressWhileUpdatingSupplier() {
        Address address = new Address();
        address.id = addressId;
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = "15000";
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.id = Long.valueOf(supplierId);
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESALER;
        supplier.description = UPDATED_DESCRIPTION;
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", supplierId)
                .when()
                .put("/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(12)
    void shouldCheckAddressIsNotUpdated() {
        Supplier supplier = given()
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", supplierId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getSupplierTypeRef());

        assertNotEquals("15000", supplier.address.postalCode);
        assertEquals(DEFAULT_POSTAL_CODE, supplier.address.postalCode);
    }

    @Test
    @Order(13)
    void shouldUpdateSupplier() {
        Supplier supplier = new Supplier();
        supplier.id = Long.valueOf(supplierId);
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESALER;
        supplier.description = UPDATED_DESCRIPTION;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", supplierId)
                .when()
                .put("/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(14)
    void shouldNotDeleteUnknownSupplier() {
        Long randomId = new Random().nextLong();
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", randomId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(15)
    void shouldDeleteSupplier() {
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .pathParam("id", supplierId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(16)
    void shouldNotCreateSupplierWhenSupplyingNullValuesForRequiredFields() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = null;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    @Order(17)
    void shouldNotCreateSupplierWhenInvalidCharatersSuppliedOnNamesFields() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = "Japhet$";
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    @Order(18)
    void shouldNotCreateSupplierWhenInvalidCharateruppliedOnEmailField() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = "technology$associate@technologyassociate-inc.com";
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    @Order(19)
    void shouldNotCreateSupplierWhenInvalidCharaterSuppliedOnMobileNumberField() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = "(255)744.111.789";
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    @Order(20)
    void shouldNotCreateSupplierWhenInvalidCharaterSuppliedOnDescriptionField() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = "Japhet gave &500";
        supplier.address = address;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(21)
    void shouldNotCreateSupplierWithoutAddress() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;

        given()
                .body(supplier)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    private TypeRef<List<Supplier>> getSuppliersTypeRef() {
        return new TypeRef<List<Supplier>>() {
        };
    }

    private TypeRef<Supplier> getSupplierTypeRef() {
        return new TypeRef<Supplier>() {
        };
    }
}
