package com.japhet_sebastian.procurement.supplier;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.AddressDto;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(SupplierResource.class)
@QuarkusTestResource(KeycloakResource.class)
class SupplierResourceTest extends AccessTokenProvider {

    @TestHTTPResource("select")
    @TestHTTPEndpoint(SupplierResource.class)
    String select;

    @Test
    void shouldGetAllSuppliers() {
        List<SupplierDto> supplier = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response().jsonPath().getList("$");

        assertThat(supplier, is(not(empty())));
        assertThat(supplier, hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldGetPagedSuppliersList() {
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<SupplierDto> suppliers = response.jsonPath().getList("$");
        assertThat(suppliers, is(not(empty())));
        assertThat(suppliers, hasSize(greaterThanOrEqualTo(1)));
        assertThat(Integer.valueOf(response.getHeader("X-Total-Count")), is(greaterThanOrEqualTo(1)));
    }

    @Test
    void getFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(select)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(greaterThanOrEqualTo(1)))
                .body(
                        containsString("Network Associate")
                );
    }

    @Test
    void getSupplierById() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // get supplier by id
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Laptop City"),
                        containsString("support@laptopcity-inc.co.tz"),
                        containsString("0800780111"),
                        containsString("ohio street Ilala, Dar es salaam")
                )
                .extract().as(SupplierDto.class);


        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));
    }

    @Test
    void shouldGetSupplierNotFound() {
        final String stringUUID = UUID.randomUUID().toString();
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(stringUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldSaveSupplier() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier saved");
        supplier.setCompanyPhone("255744608510");
        supplier.setCompanyEmail("customercare@saved.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier saved"),
                        containsString("customercare@saved.com"),
                        containsString("255744608510")
                )
                .extract().as(SupplierDto.class);


        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));
    }

    @Test
    void shouldSaveSupplierFailsMissingRequiredFields() {
        // create supplier
        final SupplierDto supplier = new SupplierDto();
        final AddressDto address = createAddress();
        supplier.setAddress(address);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(4)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("createSupplier.supplier.companyName", getErrorMessage("Supplier.company-name.required")),
                        new ErrorResponse.ErrorMessage("createSupplier.supplier.companyEmail", getErrorMessage("Email.required")),
                        new ErrorResponse.ErrorMessage("createSupplier.supplier.companyPhone", getErrorMessage("Phone.number.required")),
                        new ErrorResponse.ErrorMessage("createSupplier.supplier.supplierType", getErrorMessage("Supplier.category.required"))
                )
        );
    }

    @Test
    void shouldSaveSupplierViolateUniqueConstraint() {
        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier saved");
        supplier.setCompanyPhone("255744608510");
        supplier.setCompanyEmail("customercare@saved.com");
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), contains(
                        new ErrorResponse.ErrorMessage("Email or phone number is taken")
                )
        );
    }

    @Test
    void shouldSaveSupplierFailsNoAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        // create supplier
        final SupplierDto supplier = new SupplierDto();
        supplier.setCompanyName("Supplier no address");
        supplier.setCompanyPhone("255744608511");
        supplier.setCompanyEmail("customercare@address.com");
        supplier.setSupplierType(SupplierType.WHOLESALER);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), both(matchesRegex(UUID_REGEX)).and(notNullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), contains(
                        new ErrorResponse.ErrorMessage(getErrorMessage("System.error"))
                )
        );
    }

    @Test
    void shouldUpdateSupplier() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier create");
        supplier.setCompanyPhone("255744608513");
        supplier.setCompanyEmail("customercare@create.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier create"),
                        containsString("customercare@create.com"),
                        containsString("255744608513")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier
        supplier.setSupplierId(foundSupplier.getSupplierId());
        supplier.setCompanyName("Supplier updated");
        supplier.setCompanyPhone("255744608515");
        supplier.setCompanyEmail("customercare@updated.com");
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm supplier is updated
        SupplierDto updatedSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier updated"),
                        containsString("customercare@updated.com"),
                        containsString("255744608515")
                )
                .extract().as(SupplierDto.class);

        assertThat(updatedSupplier, is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(updatedSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(updatedSupplier.getUpdatedBy(), allOf(is(not(nullValue())), is(equalTo("habiba.baanda"))));
    }

    @Test
    void shouldUpdateAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier for address");
        supplier.setCompanyPhone("255744608525");
        supplier.setCompanyEmail("customercare@address.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier for address"),
                        containsString("customercare@address.com"),
                        containsString("255744608525")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        AddressDto address = new AddressDto();
        address.setStreet("Chaburuma street");
        address.setDistrict("Kinondoni");
        address.setPostalCode("16772");
        supplier.setSupplierId(foundSupplier.getSupplierId());
        supplier.setAddress(address);
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm supplier is updated
        SupplierDto updatedSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Chaburuma street Kinondoni, Dar es salaam")
                )
                .extract().as(SupplierDto.class);

        assertThat(updatedSupplier, is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(updatedSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(updatedSupplier.getUpdatedBy(), allOf(is(not(nullValue())), is(equalTo("habiba.baanda"))));
    }

    @Test
    void shouldUpdateSupplierAndAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier for both");
        supplier.setCompanyPhone("255744608527");
        supplier.setCompanyEmail("customercare@both.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier for both"),
                        containsString("customercare@both.com"),
                        containsString("255744608527")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        AddressDto address = new AddressDto();
        address.setStreet("Chaburuma updated");
        address.setDistrict("Kinondoni");
        address.setPostalCode("16772");
        supplier.setSupplierId(foundSupplier.getSupplierId());
        supplier.setAddress(address);
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm supplier is updated
        SupplierDto updatedSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Chaburuma updated Kinondoni, Dar es salaam")
                )
                .extract().as(SupplierDto.class);

        assertThat(updatedSupplier, is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(updatedSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(updatedSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(updatedSupplier.getUpdatedBy(), allOf(is(not(nullValue())), is(equalTo("habiba.baanda"))));
    }

    @Test
    void shouldFailUpdateSupplierNullId() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier null ID");
        supplier.setCompanyPhone("255744608528");
        supplier.setCompanyEmail("customercare@null.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier null ID"),
                        containsString("customercare@null.com"),
                        containsString("255744608528")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        supplier.setSupplierId(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Supplier does not have supplierId"))
        );

    }

    @Test
    void shouldFailUpdateSupplierEmptyIdString() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier empty ID");
        supplier.setCompanyPhone("255744608529");
        supplier.setCompanyEmail("customercare@empty.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier empty ID"),
                        containsString("customercare@empty.com"),
                        containsString("255744608529")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        supplier.setSupplierId("");
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Supplier does not have supplierId"))
        );

    }

    @Test
    void shouldFailUpdateSupplierIdMismatch() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier ID mismatch");
        supplier.setCompanyPhone("255744608530");
        supplier.setCompanyEmail("customercare@mismatch.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier ID mismatch"),
                        containsString("customercare@mismatch.com"),
                        containsString("255744608530")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        supplier.setSupplierId(UUID.randomUUID().toString());
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("path variable supplierId does not match Supplier.supplierId"))
        );

    }

    @Test
    void shouldFailsUpdateSupplierNullAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier no address");
        supplier.setCompanyPhone("255744608544");
        supplier.setCompanyEmail("customercare@none.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier no address"),
                        containsString("customercare@none.com"),
                        containsString("255744608544")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        supplier.setSupplierId(foundSupplier.getSupplierId());
        supplier.setAddress(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Address is missing"))
        );
    }

    @Test
    void shouldFailsUpdateSupplierConstraintViolation() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier no name");
        supplier.setCompanyPhone("255744608545");
        supplier.setCompanyEmail("customercare@name.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier no name"),
                        containsString("customercare@name.com"),
                        containsString("255744608545")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // update supplier address
        supplier.setSupplierId(foundSupplier.getSupplierId());
        supplier.setCompanyName(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(supplier).put(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), contains(
                new ErrorResponse.ErrorMessage("updateSupplier.supplier.companyName", getErrorMessage("Supplier.company-name.required")))
        );
    }

    @Test
    void shouldDeleteSupplier() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName("Supplier delete");
        supplier.setCompanyPhone("255744608547");
        supplier.setCompanyEmail("customercare@delete.com");
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // check created supplier
        SupplierDto foundSupplier = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(supplierUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Supplier delete"),
                        containsString("customercare@delete.com"),
                        containsString("255744608547")
                )
                .extract().as(SupplierDto.class);

        assertThat(foundSupplier, is(notNullValue()));
        assertThat(foundSupplier.getRegisteredAt(), is(notNullValue()));
        assertThat(foundSupplier.getUpdatedAt(), is(notNullValue()));
        assertThat(foundSupplier.getRegisteredBy(), is(equalTo("lulu.shaban")));
        assertThat(foundSupplier.getUpdatedBy(), is(nullValue()));

        // DELETE supplier
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().delete(foundSupplier.getSupplierId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // verify supplier deleted
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(supplierUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void deleteSupplierFailsNotFound() {
        String supplierUUID = UUID.randomUUID().toString();
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().delete(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(String.format("No supplier found for supplierId[%s]", supplierUUID)))
        );
    }

    private SupplierDto createSupplier() {
        final AddressDto address = createAddress();

        final SupplierDto supplier = new SupplierDto();
        supplier.setCompanyName("Laptop City");
        supplier.setCompanyEmail("support@laptopcity-inc.co.tz");
        supplier.setCompanyPhone("0800780111");
        supplier.setSupplierType(SupplierType.RETAILER);
        supplier.setAddress(address);
        return supplier;
    }

    private AddressDto createAddress() {
        final AddressDto address = new AddressDto();
        address.setStreet("ohio street");
        address.setPostalCode("19119");
        address.setDistrict("Ilala");
        address.setCity("Dar es salaam");
        address.setCountry("Tanzania");
        return address;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }

}