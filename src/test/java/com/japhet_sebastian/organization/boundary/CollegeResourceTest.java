package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.College;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(CollegeResource.class)
@QuarkusTestResource(KeycloakResource.class)
class CollegeResourceTest extends AccessTokenProvider {

    @Inject
    CollegeAddressMapper collegeAddressMapper;

    @TestHTTPResource("collegeId")
    @TestHTTPEndpoint(CollegeResource.class)
    java.lang.String stringId;

    @TestHTTPResource("select")
    @TestHTTPEndpoint(CollegeResource.class)
    java.lang.String selectPath;

    @Test
    @Order(1)
    void getAll() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(2),
                        "collegeCode", containsInAnyOrder("CoAF", "CoICT"))
                .header("X-Total-Count", java.lang.String.valueOf(2));
    }

    @Test
    @Order(2)
    void getPagedList() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(1),
                        "collegeCode", anyOf(contains("CoAF"), contains("CoICT")))
                .header("X-Total-Count", java.lang.String.valueOf(2));
    }

    @Test
    @Order(3)
    void getFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(selectPath)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(2),
                        "label", containsInAnyOrder(
                                "College of Agricultural Sciences and Fisheries",
                                "College of Information and Communication Technology"));
    }

    @Test
    @Order(4)
    void getById() {
        final java.lang.String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeAddress collegeAddress = createCollegeAddress();
        java.lang.String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        java.lang.String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeAddress found = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeAddress.class);

        assertThat(uuid, equalTo(found.getAddress().getAddressId()));
    }

    @Test
    @Order(5)
    void getNotFound() {
        final java.lang.String uuid = "450921b5-5def-4fab-b3f6-2bea30ee7099";
        given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .contentType(ContentType.JSON)
                .when().get(uuid)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(6)
    void saveCollege() {
        final java.lang.String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeAddress collegeAddress = createCollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeCode("CoNAS");
        java.lang.String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, is(notNullValue()));
        java.lang.String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));
    }

    @Test
    @Order(7)
    void saveFailsNoCollegeName() {
        CollegeAddress collegeAddress = createCollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeName(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("createCollege.collegeAddress.collegeName",
                        getErrorMessage("College.name.required"))));
    }

    @Test
    @Order(8)
    void saveViolatesUniqueConstraint() {
        CollegeAddress collegeAddress = createCollegeAddress();
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(notNullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(
                        getErrorMessage("System.error"))));

    }

    @Test
    @Order(9)
    void saveConstraintViolations() {
        CollegeAddress collegeAddress = createCollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeName(null);
        collegeAddress.getAddress().getCollege().setCollegeCode(RandomStringUtils.randomAlphanumeric(12));
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);


        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(2)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                new ErrorResponse.ErrorMessage("createCollege.collegeAddress.collegeName", getErrorMessage("College.name.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeAddress.collegeCode", getErrorMessage("Alphanumeric.character.length")))
        );
    }

    @Test
    @Order(10)
    void updateCollege() {
        final java.lang.String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeAddress collegeAddress = createCollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeName("Original college name before update");
        java.lang.String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeAddress.getAddress().getCollege().getCollegeName(), is(equalTo("Original college name before update")));
        java.lang.String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeAddress found = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeAddress.class);

        assertThat(uuid, equalTo(found.getAddress().getAddressId()));

        College updateString = this.collegeAddressMapper.toCollege(found);
        updateString.setCollegeName("Updated college name");

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(updateString)
                .when().put(updateString.getCollegeId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        CollegeAddress updated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeAddress.class);

        assertThat(updated.getAddress().getCollege().getCollegeName(), is(equalTo("Updated college name")));
    }

    @Test
    @Order(11)
    void updateFailsNoCollegeName() {
        final java.lang.String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeAddress collegeAddress = createCollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeName("This update should fail");
        java.lang.String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeAddress)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeAddress.getAddress().getCollege().getCollegeName(), is(equalTo("This update should fail")));
        java.lang.String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeAddress found = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeAddress.class);

        assertThat(uuid, equalTo(found.getAddress().getAddressId()));

        College updateString = this.collegeAddressMapper.toCollege(found);
        updateString.setCollegeName(null);

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(updateString)
                .when().put(updateString.getCollegeId())
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("updateCollege.college.collegeName",
                        getErrorMessage("College.name.required")))
        );
    }

    private CollegeAddress createCollegeAddress() {
        Address address = new Address();
        address.setStreet("Chuo kikuu");
        address.setWard("Ubungo");
        address.setPostalCode("15114");
        address.setDistrict("Ubungo");
        address.setCity("Dar es salaam");
        address.setCountry("Tanzania");

        CollegeAddress collegeAddress = new CollegeAddress();
        collegeAddress.getAddress().getCollege().setCollegeName("College of Engineering Technology");
        collegeAddress.getAddress().getCollege().setCollegeCode("CoET");
        collegeAddress.setAddress(address);
        return collegeAddress;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}