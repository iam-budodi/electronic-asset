package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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

    @TestHTTPResource("collegeId")
    @TestHTTPEndpoint(CollegeResource.class)
    String stringId;

    @TestHTTPResource("select")
    @TestHTTPEndpoint(CollegeResource.class)
    String selectPath;

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
                .header("X-Total-Count", String.valueOf(2));
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
                .header("X-Total-Count", String.valueOf(2));
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
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeDetail collegeDetail = createCollegeDetail();
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));
    }

    @Test
    @Order(5)
    void getNotFound() {
        final String uuid = "450921b5-5def-4fab-b3f6-2bea30ee7099";
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
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeCode("CoNAS");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, is(notNullValue()));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));
    }

    @Test
    @Order(7)
    void saveFailsNoCollegeName() {
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("createCollege.collegeDetail.collegeName",
                        getErrorMessage("College.name.required"))));
    }

    @Test
    @Order(8)
    void saveViolatesUniqueConstraint() {
        CollegeDetail collegeDetail = createCollegeDetail();
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
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
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(null);
        collegeDetail.setCollegeCode(RandomStringUtils.randomAlphanumeric(12));
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);


        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(2)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.collegeName", getErrorMessage("College.name.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.collegeCode", getErrorMessage("Alphanumeric.character.length")))
        );
    }

    @Test
    @Order(10)
    void updateCollege() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Original college name before update");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo("Original college name before update")));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));

        foundCollegeDetail.setCollegeName("Updated college name");

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollegeDetail)
                .when().put(foundCollegeDetail.getCollegeId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        CollegeDetail collegeDetailUpdated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailUpdated.getCollegeName(), is(equalTo("Updated college name")));
    }

    @Test
    @Order(11)
    void updateFailsNoCollegeName() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("This update should fail");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo("This update should fail")));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));

        foundCollegeDetail.setCollegeName(null);

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollegeDetail)
                .when().put(foundCollegeDetail.getCollegeId())
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("updateCollege.collegeDetail.collegeName",
                        getErrorMessage("College.name.required")))
        );
    }

    private CollegeDetail createCollegeDetail() {
        CollegeDetail collegeDetail = new CollegeDetail();
        collegeDetail.setCollegeName("College of Engineering Technology");
        collegeDetail.setCollegeCode("CoET");
        collegeDetail.setStreet("Chuo kikuu");
        collegeDetail.setWard("Ubungo");
        collegeDetail.setPostalCode("15114");
        collegeDetail.setDistrict("Ubungo");
        collegeDetail.setCity("Dar es salaam");
        collegeDetail.setCountry("Tanzania");
        return collegeDetail;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}