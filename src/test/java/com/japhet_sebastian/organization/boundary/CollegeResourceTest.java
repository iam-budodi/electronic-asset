package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.AddressDto;
import com.japhet_sebastian.organization.entity.CollegeDto;
import com.japhet_sebastian.organization.entity.DepartmentDto;
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

        List<CollegeDto> colleges = response.jsonPath().getList("$");
        String totalElement = response.getHeader("X-Total-Count");
        assertThat(colleges, is(not(empty())));
        assertThat(colleges, hasSize(greaterThanOrEqualTo(2)));
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

        List<CollegeDto> colleges = response.jsonPath().getList("$");
        String totalElement = response.getHeader("X-Total-Count");
        assertThat(colleges, is(not(empty())));
        assertThat(colleges, hasSize(1));
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
        final CollegeDto college = createCollege();
        college.setCollegeName("College of Geology");
        college.setCollegeCode("CoGE");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("College of Geology"),
                        containsString("CoGE"),
                        containsString("Chuo kikuu Ubungo, Dar es salaam")
                )
                .extract().as(CollegeDto.class);

        assertThat(foundCollege, is(notNullValue()));
        assertThat(uuid, equalTo(foundCollege.getCollegeId()));
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
        CollegeDto college = createCollege();
        college.setCollegeCode("CoNAS");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, is(notNullValue()));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("College of Engineering Technology"),
                        containsString("CoNAS"),
                        containsString("Chuo kikuu Ubungo, Dar es salaam")
                )
                .extract().as(CollegeDto.class);

        assertThat(foundCollege, is(notNullValue()));
        assertThat(uuid, equalTo(foundCollege.getCollegeId()));
    }

    @Test
    void saveFailsNoCollegeName() {
        CollegeDto college = createCollege();
        college.setCollegeName(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("createCollege.collegeDto.collegeName",
                        getErrorMessage("College.name.required"))));
    }

    @Test
    void saveViolatesUniqueConstraint() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        final CollegeDto college = createCollege();
        college.setCollegeName("College of Duplicate");
        college.setCollegeCode("CoDU");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, is(notNullValue()));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // duplicates colleges
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("College already exists")
                )
        );
    }

    @Test
    void saveConstraintViolations() {
        CollegeDto college = createCollege();
        college.setCollegeName(null);
        college.setCollegeCode(RandomStringUtils.randomAlphanumeric(12));
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(2)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                new ErrorResponse.ErrorMessage("createCollege.collegeDto.collegeName", getErrorMessage("College.name.required")),
                new ErrorResponse.ErrorMessage("createCollege.collegeDto.collegeCode", getErrorMessage("Alphanumeric.character.length")))
        );
    }

    @Test
    void saveFailValidationsConstraintViolations() {
        CollegeDto college = new CollegeDto();
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(college).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("createCollege.collegeDto.collegeName", getErrorMessage("College.name.required"))
                )
        );
    }

    @Test
    void updateOnlyCollege() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update";
        final String collegeNameUpdated = "Updated college name";
        CollegeDto college = createCollege();
        college.setCollegeName(collegeName);

        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo("Original college name before update")));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(uuid, equalTo(foundCollege.getCollegeId()));
        assertThat(foundCollege.getCollegeName(), is(equalTo(collegeName)));

        foundCollege.setCollegeName(collegeNameUpdated);

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollege)
                .when().put(foundCollege.getCollegeId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        CollegeDto collegeDtoUpdated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString(collegeNameUpdated),
                        containsString("CoET"),
                        containsString("Chuo kikuu Ubungo, Dar es salaam")
                )
                .extract().as(CollegeDto.class);

        assertThat(collegeDtoUpdated.getCollegeName(), is(equalTo(collegeNameUpdated)));
    }

    @Test
    void updateFailsNoCollegeName() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        CollegeDto college = createCollege();
        college.setCollegeName("This update should fail");
        college.setCollegeCode("FAILS");
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo("This update should fail")));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(uuid, equalTo(foundCollege.getCollegeId()));

        foundCollege.setCollegeName(null);

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollege)
                .when().put(foundCollege.getCollegeId())
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("updateCollege.collegeDto.collegeName",
                        getErrorMessage("College.name.required")))
        );
    }

    @Test
    void updateOnlyAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update address";
        final String addressStreetUpdated = "Main campus";
        CollegeDto college = createCollege();
        college.setCollegeName(collegeName);
        college.setCollegeCode("MAIN");

        // create college
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // check created college
        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(uuid, equalTo(foundCollege.getCollegeId()));
        assertThat(foundCollege.getCollegeName(), is(equalTo(collegeName)));

        // update college address
        college.getAddress().setStreet(addressStreetUpdated);
        foundCollege.setAddress(college.getAddress());

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollege)
                .when().put(foundCollege.getCollegeId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm it is updated
        CollegeDto collegeDtoUpdated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString(collegeName),
                        containsString("MAIN"),
                        containsString(addressStreetUpdated)
                )
                .extract().as(CollegeDto.class);

        assertThat(collegeDtoUpdated.getCollegeName(), is(equalTo(collegeName)));
        assertThat(uuid, is(equalTo(collegeDtoUpdated.getCollegeId())));
    }

    @Test
    void updateBothCollegeAndAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "Original college name before update college and address";
        final String addressStreetUpdated = "Mlimani main campus";
        final String collegeNameUpdated = "Updated college name and street address";
        CollegeDto college = createCollege();
        college.setCollegeName(collegeName);
        college.setCollegeCode("Original");

        // create college
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // check created college
        CollegeDto foundCollege = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(uuid, equalTo(foundCollege.getCollegeId()));
        assertThat(foundCollege.getCollegeName(), is(equalTo(collegeName)));

        // update college and address
        foundCollege.setCollegeName(collegeNameUpdated);
        college.getAddress().setStreet(addressStreetUpdated);
        foundCollege.setAddress(college.getAddress());

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(foundCollege)
                .when().put(foundCollege.getCollegeId())
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm updates
        CollegeDto collegeDtoUpdated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString(collegeNameUpdated),
                        containsString("Original"),
                        containsString(addressStreetUpdated)
                )
                .extract().as(CollegeDto.class);

        assertThat(collegeDtoUpdated.getCollegeName(), is(equalTo(collegeNameUpdated)));
        assertThat(collegeDtoUpdated.getCollegeAddress(), containsString(addressStreetUpdated));
    }

    @Test
    void deleteCollegeAndAssociatedAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "To be deleted college and address";

        CollegeDto college = createCollege();
        college.setCollegeName(collegeName);
        college.setCollegeCode("DEL");

        // Get all college available
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDto> colleges = response.jsonPath().getList("$");

        assertThat(colleges, is(not(empty())));
        assertThat(colleges, hasSize(greaterThanOrEqualTo(2)));

        // creates new college
        String url = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(url, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo(collegeName)));
        String uuid = url.substring(url.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));

        // Get new list of all college available
        Response results = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDto> newCollegeList = results.jsonPath().getList("$");

        assertThat(newCollegeList, is(not(empty())));
        assertThat(newCollegeList, hasSize(greaterThanOrEqualTo(4))); // increased by 1

        CollegeDto collegeDtoFound = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeDtoFound, is(notNullValue()));
        assertThat(uuid, is(equalTo(collegeDtoFound.getCollegeId())));
        assertThat(collegeDtoFound.getCollegeName(), is(equalTo(collegeName)));

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

        List<CollegeDto> newDeletedList = deleted.jsonPath().getList("$");

        assertThat(newDeletedList, is(not(empty())));
        assertThat(newDeletedList, hasSize(greaterThanOrEqualTo(3))); // reduced by 1
    }

    @Test
    void deleteCollegeAndAssociatedAddressAndDepartment() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        final String collegeName = "To be deleted college and address";

        CollegeDto college = createCollege();
        college.setCollegeName(collegeName);
        college.setCollegeCode("COLLEGE");

        // Get all colleges available
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<CollegeDto> colleges = response.jsonPath().getList("$");
        assertThat(colleges, is(not(empty())));
        assertThat(colleges, hasSize(greaterThanOrEqualTo(2)));

        // creates new college
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(college).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        assertThat(college.getCollegeName(), is(equalTo(collegeName)));
        String uuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(uuid, matchesRegex(UUID_REGEX));


        // get created college
        CollegeDto collegeCreated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(collegeUrl)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        // add new department to a created college
        DepartmentDto departmentData = createDepartment();
        collegeCreated.setAddress(college.getAddress());
        departmentData.setCollege(collegeCreated);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentData).post(department)
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

        List<CollegeDto> newCollegeList = results.jsonPath().getList("$");

        assertThat(newCollegeList, is(not(empty())));
        assertThat(newCollegeList, hasSize(greaterThanOrEqualTo(3))); // increased by 1

        CollegeDto collegeDtoFound = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(uuid)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeDtoFound, is(notNullValue()));
        assertThat(uuid, is(equalTo(collegeDtoFound.getCollegeId())));
        assertThat(collegeDtoFound.getCollegeName(), is(equalTo(collegeName)));

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

        List<CollegeDto> newDeletedList = deleted.jsonPath().getList("$");

        assertThat(newDeletedList, is(not(empty())));
        assertThat(newDeletedList, hasSize(greaterThanOrEqualTo(2))); // reduced by 1
    }

    private CollegeDto createCollege() {
        final AddressDto addressDto = new AddressDto();
        addressDto.setStreet("Chuo kikuu");
        addressDto.setPostalCode("15114");
        addressDto.setDistrict("Ubungo");
        addressDto.setCity("Dar es salaam");
        addressDto.setCountry("Tanzania");

        final CollegeDto college = new CollegeDto();
        college.setCollegeName("College of Engineering Technology");
        college.setCollegeCode("CoET");
        college.setAddress(addressDto);
        return college;
    }

    private DepartmentDto createDepartment() {
        final DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentName("Deleted Dept");
        departmentDto.setDepartmentCode("DEL012");
        departmentDto.setCollege(null);
        return departmentDto;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}