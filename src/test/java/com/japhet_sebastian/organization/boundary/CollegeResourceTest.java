package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(CollegeResource.class)
@QuarkusTestResource(KeycloakResource.class)
class CollegeResourceTest extends AccessTokenProvider {

    @TestHTTPResource("select")
    @TestHTTPEndpoint(CollegeResource.class)
    String selectPath;

    @TestHTTPResource()
    @TestHTTPEndpoint(DepartmentResource.class)
    String department;

    @Test
    void getAll() {
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> collegeDetails = response.jsonPath().getList("$");
        String totalElement = response.getHeader("X-Total-Count");
        assertThat(collegeDetails, is(not(empty())));
        assertThat(collegeDetails, hasSize(greaterThanOrEqualTo(2)));
        assertThat(Integer.valueOf(totalElement), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void getPagedList() {
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> collegeDetails = response.jsonPath().getList("$");
        String totalElement = response.getHeader("X-Total-Count");
        assertThat(collegeDetails, is(not(empty())));
        assertThat(collegeDetails, hasSize(1));
        assertThat(Integer.valueOf(totalElement), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void getFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(selectPath)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(greaterThanOrEqualTo(2)))
                .body(
                        containsString("College of Agricultural Sciences and Fisheries"),
                        containsString("College of Information and Communication Technology")
                );
    }

    @Test
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
                .body(
                        containsString("College of Engineering Technology"),
                        containsString("CoET"),
                        containsString("Chuo kikuu"),
                        containsString("Ubungo"),
                        containsString("15114"),
                        containsString("Dar es salaam"),
                        containsString("Tanzania")
                )
                .extract().as(CollegeDetail.class);

        assertThat(foundCollegeDetail, is(notNullValue()));
        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));
    }

    @Test
    void getNotFound() {
        final String randomUuidString = UUID.randomUUID().toString();
        given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .contentType(ContentType.JSON)
                .when().get(randomUuidString)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
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

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("College of Engineering Technology"),
                        containsString("CoNAS"),
                        containsString("Chuo kikuu"),
                        containsString("Ubungo"),
                        containsString("15114"),
                        containsString("Dar es salaam"),
                        containsString("Tanzania")
                )
                .extract().as(CollegeDetail.class);

        assertThat(foundCollegeDetail, is(notNullValue()));
        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));
    }

    @Test
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
    void saveViolatesUniqueConstraint() {
        CollegeDetail collegeDetail = createCollegeDetail();
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail).post()
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
    void saveFailValidationsConstraintViolations() {
        CollegeDetail collegeDetail = new CollegeDetail();
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(5)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.collegeName", getErrorMessage("College.name.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.street", getErrorMessage("Address.field.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.city", getErrorMessage("Address.field.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.district", getErrorMessage("Address.field.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDetail.country", getErrorMessage("Address.field.required")))
        );
    }

    @Test
    void updateOnlyCollege() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update";
        final String collegeNameUpdated = "Updated college name";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(collegeName);

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
        assertThat(foundCollegeDetail.getCollegeName(), is(equalTo(collegeName)));

        foundCollegeDetail.setCollegeName(collegeNameUpdated);

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
                .body(
                        containsString(collegeNameUpdated),
                        containsString("CoET"),
                        containsString("Chuo kikuu"),
                        containsString("Ubungo"),
                        containsString("15114"),
                        containsString("Dar es salaam"),
                        containsString("Tanzania")
                )
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailUpdated.getCollegeName(), is(equalTo(collegeNameUpdated)));
    }

    @Test
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

    @Test
    void updateOnlyAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update address";
        final String addressStreetUpdated = "Main campus";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(collegeName);

        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));
        assertThat(foundCollegeDetail.getCollegeName(), is(equalTo(collegeName)));

        foundCollegeDetail.setStreet(addressStreetUpdated);

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
                .body(
                        containsString(collegeName),
                        containsString("CoET"),
                        containsString(addressStreetUpdated),
                        containsString("Ubungo"),
                        containsString("15114"),
                        containsString("Dar es salaam"),
                        containsString("Tanzania")
                )
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailUpdated.getCollegeName(), is(equalTo(collegeName)));
        assertThat(uuid, is(equalTo(collegeDetailUpdated.getCollegeId())));
    }

    @Test
    void updateBothCollegeAndAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update college and address";
        final String addressStreetUpdated = "Mlimani main campus";
        final String collegeNameUpdated = "Updated college name and street address";
        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(collegeName);

        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDetail foundCollegeDetail = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(uuid, equalTo(foundCollegeDetail.getCollegeId()));
        assertThat(foundCollegeDetail.getCollegeName(), is(equalTo(collegeName)));

        foundCollegeDetail.setCollegeName(collegeNameUpdated);
        foundCollegeDetail.setStreet(addressStreetUpdated);

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
                .body(
                        containsString(collegeNameUpdated),
                        containsString("CoET"),
                        containsString(addressStreetUpdated),
                        containsString("Ubungo"),
                        containsString("15114"),
                        containsString("Dar es salaam"),
                        containsString("Tanzania")
                )
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailUpdated.getCollegeName(), is(equalTo(collegeNameUpdated)));
        assertThat(collegeDetailUpdated.getStreet(), is(equalTo(addressStreetUpdated)));
    }

    @Test
    void deleteCollegeAndAssociatedAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "To be deleted college and address";

        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(collegeName);

        // Get all college available
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> collegeDetails = response.jsonPath().getList("$");

        assertThat(collegeDetails, is(not(empty())));
        assertThat(collegeDetails, hasSize(greaterThanOrEqualTo(3)));

        // creates new college
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // Get new list of all college available
        Response results = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> newCollegeDetailsLis = results.jsonPath().getList("$");

        assertThat(newCollegeDetailsLis, is(not(empty())));
        assertThat(newCollegeDetailsLis, hasSize(greaterThanOrEqualTo(4))); // increased by 1

        CollegeDetail collegeDetailFound = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailFound, is(notNullValue()));
        assertThat(uuid, is(equalTo(collegeDetailFound.getCollegeId())));
        assertThat(collegeDetailFound.getCollegeName(), is(equalTo(collegeName)));

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().delete(uuid)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        // Get new list of all college available after deletion
        Response deleted = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> newDeletedList = deleted.jsonPath().getList("$");

        assertThat(newDeletedList, is(not(empty())));
        assertThat(newDeletedList, hasSize(greaterThanOrEqualTo(3))); // reduced by 1
    }


    @Test
    void deleteCollegeAndAssociatedAddressAndDepartment() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "To be deleted college and address";

        CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName(collegeName);

        // Get all colleges available
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> collegeDetails = response.jsonPath().getList("$");
        assertThat(collegeDetails, is(not(empty())));
        assertThat(collegeDetails, hasSize(greaterThanOrEqualTo(2)));

        // creates new college
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        assertThat(collegeDetail.getCollegeName(), is(equalTo(collegeName)));
        String uuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // add new department to a created colleger
        DepartmentInput departmentInput = createDepartment();
        departmentInput.setCollegeId(uuid);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUUID = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get new list of all college available
        Response results = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> newCollegeDetailsLis = results.jsonPath().getList("$");

        assertThat(newCollegeDetailsLis, is(not(empty())));
        assertThat(newCollegeDetailsLis, hasSize(greaterThanOrEqualTo(3))); // increased by 1

        CollegeDetail collegeDetailFound = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDetail.class);

        assertThat(collegeDetailFound, is(notNullValue()));
        assertThat(uuid, is(equalTo(collegeDetailFound.getCollegeId())));
        assertThat(collegeDetailFound.getCollegeName(), is(equalTo(collegeName)));

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().delete(uuid)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());


        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .pathParam("departmentId", departmentUUID)
                .get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        // Get new list of all college available after deletion
        Response deleted = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDetail> newDeletedList = deleted.jsonPath().getList("$");

        assertThat(newDeletedList, is(not(empty())));
        assertThat(newDeletedList, hasSize(greaterThanOrEqualTo(2))); // reduced by 1
    }

    private CollegeDetail createCollegeDetail() {
        CollegeDetail collegeDetail = new CollegeDetail();
        collegeDetail.setCollegeName("College of Engineering Technology");
        collegeDetail.setCollegeCode("CoET");
        collegeDetail.setStreet("Chuo kikuu");
        collegeDetail.setPostalCode("15114");
        collegeDetail.setDistrict("Ubungo");
        collegeDetail.setCity("Dar es salaam");
        collegeDetail.setCountry("Tanzania");
        return collegeDetail;
    }

    private DepartmentInput createDepartment() {
        DepartmentInput departmentInput = new DepartmentInput();
        departmentInput.setDepartmentName("Deleted Dept");
        departmentInput.setDepartmentCode("DEL012");
        departmentInput.setCollegeId(null);
        return departmentInput;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}