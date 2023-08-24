package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(DepartmentResource.class)
@QuarkusTestResource(KeycloakResource.class)
class DepartmentResourceTest extends AccessTokenProvider {

    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    @TestHTTPResource()
    @TestHTTPEndpoint(CollegeResource.class)
    String college;

    @TestHTTPResource("select")
    @TestHTTPEndpoint(DepartmentResource.class)
    String selectPath;

    @Test
    void shouldGetDepartments() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .body("$", is(not(empty())))
                .body("size()", is(2))
                .body(
                        containsString("TE"),
                        containsString("CSE")
                )
                .header("X-Total-Count", String.valueOf(2));
    }

    @Test
    void shouldGetPagedList() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(1))
                .body("departmentCode", anyOf(contains("CSE"), contains("TE")))
                .header("X-Total-Count", String.valueOf(2));
    }
//
//    @Test
//    void shouldGetFormSelectionOptions() {
//        given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .when().get(selectPath)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("$", hasSize(greaterThanOrEqualTo(2)))
//                .body("label", containsInAnyOrder("Computer Science and Engineering",
//                        "Telecommunication Engineering"));
//    }
//
//    @Test
//    void shouldGetDepartmentById() {
//        CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        DepartmentInput departmentInput = createDepartment();
//        departmentInput.setCollegeId(collegeUuid);
//        String departmentUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(departmentUrl, notNullValue());
//        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
//        assertThat(departmentUuid, matchesRegex(UUID_REGEX));
//
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("departmentId", is(equalTo(departmentUuid)))
//                .body("departmentName", is(equalTo("Department of Electronics and Telecommunication Engineering")))
//                .body("departmentCode", is(equalTo("ETE")));
//    }
//
//    @Test
//    void shouldNotFindDepartment() {
//        final String randomUuidString = UUID.randomUUID().toString();
//        given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .when().get(randomUuidString)
//                .then()
//                .statusCode(NOT_FOUND.getStatusCode());
//    }
//
//    @Test
//    void shouldSaveDepartment() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = createDepartment();
//        departmentInput.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
//        departmentInput.setDepartmentCode(RandomStringUtils.randomAlphanumeric(10));
//        departmentInput.setCollegeId(collegeUuid);
//        String departmentUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(departmentUrl, notNullValue());
//        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
//        assertThat(departmentUuid, matchesRegex(UUID_REGEX));
//
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("departmentId", is(equalTo(departmentUuid)));
//    }
//
//    @Test
//    void shouldFailOnSaveNoDepartmentName() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = createDepartment();
//        departmentInput.setDepartmentName(null);
//        departmentInput.setCollegeId(collegeUuid);
//        ErrorResponse errorResponse = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrorId(), is(nullValue()));
//        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
//        assertThat(errorResponse.getErrors(),
//                contains(new ErrorResponse.ErrorMessage("saveDepartment.departmentInput.departmentName",
//                        getErrorMessage("Department.field.required"))));
//    }
//
//    @Test
//    @Order(8)
//    void shouldFailOnSaveNoCollegeId() {
//        final DepartmentInput departmentInput = createDepartment();
//        departmentInput.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
//        departmentInput.setCollegeId(null);
//        ErrorResponse errorResponse = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrorId(), is(notNullValue()));
//        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
//        assertThat(errorResponse.getErrors(),
//                contains(new ErrorResponse.ErrorMessage(getErrorMessage("System.error"))));
//    }
//
//    @Test
//    void shouldFailOnSaveViolatesUniqueConstraint() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = createDepartment();
//        departmentInput.setCollegeId(collegeUuid);
//        ErrorResponse errorResponse = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrorId(), is(nullValue()));
//        assertThat(errorResponse.getErrors(), hasSize(1));
//        assertThat(errorResponse.getErrors(),
//                contains(new ErrorResponse.ErrorMessage("Department with same name already exists")));
//    }
//
//    @Test
//    void shouldFailOnSaveConstraintViolations() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = createDepartment();
//        departmentInput.setDepartmentName(null);
//        departmentInput.setDepartmentCode(RandomStringUtils.randomAlphanumeric(12));
//        departmentInput.setCollegeId(collegeUuid);
//        ErrorResponse errorResponse = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(2)));
//        assertThat(errorResponse.getErrors(), containsInAnyOrder(
//                new ErrorResponse.ErrorMessage("saveDepartment.departmentInput.departmentName", getErrorMessage("Department.field.required")),
//                new ErrorResponse.ErrorMessage("saveDepartment.departmentInput.departmentCode", getErrorMessage("Alphanumeric.character.length")))
//        );
//    }
//
//    @Test
//    void updateDepartment() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = new DepartmentInput();
//        departmentInput.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
//        departmentInput.setDepartmentCode(RandomStringUtils.randomAlphanumeric(10));
//        departmentInput.setCollegeId(collegeUuid);
//        String departmentUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(departmentUrl, notNullValue());
//        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
//        assertThat(departmentUuid, matchesRegex(UUID_REGEX));
//
//        DepartmentDetail departmentInputSaved = given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("departmentId", is(equalTo(departmentUuid)))
//                .extract().as(DepartmentDetail.class);
//
//        String departmentNameUpdated = "Dept of Electronics and Telecommunication Engineering - Update";
//        departmentInput.setDepartmentId(departmentUuid);
//        departmentInput.setCollegeId(collegeUuid);
//        departmentInput.setDepartmentName(departmentNameUpdated);
//
//        given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .body(departmentInput)
//                .when().put(departmentUuid)
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//
//        DepartmentDetail departmentInputUpdated = given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body(
//                        containsString(departmentNameUpdated),
//                        containsString(collegeDetail.getCollegeName())
//                )
//                .extract().as(DepartmentDetail.class);
//
//        assertThat(departmentNameUpdated, is(equalTo(departmentInputUpdated.getDepartmentName())));
//        assertThat(departmentInputSaved.getDepartmentId(), is(equalTo(departmentInputUpdated.getDepartmentId())));
//        assertThat(departmentInputSaved.getDepartmentName(), is(not(equalTo(departmentInputUpdated.getDepartmentName()))));
//        assertThat(departmentInputSaved.getCollegeName(), is(equalTo(departmentInputUpdated.getCollegeName())));
//    }
//
//    @Test
//    void updateDepartmentFailsNoDepartmentName() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = new DepartmentInput();
//        departmentInput.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
//        departmentInput.setDepartmentCode(RandomStringUtils.randomAlphanumeric(10));
//        departmentInput.setCollegeId(collegeUuid);
//        String departmentUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(departmentUrl, notNullValue());
//        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
//        assertThat(departmentUuid, matchesRegex(UUID_REGEX));
//
//        departmentInput.setDepartmentId(departmentUuid);
//        departmentInput.setDepartmentName(null);
//
//        ErrorResponse errorResponse = given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .body(departmentInput)
//                .when().put(departmentUuid)
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrorId(), is(nullValue()));
//        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
//        assertThat(errorResponse.getErrors(),
//                contains(new ErrorResponse.ErrorMessage("updateDepartment.department.departmentName",
//                        getErrorMessage("Department.field.required")))
//        );
//    }
//
//    @Test
//    void deleteDepartment() {
//        final CollegeDetail collegeDetail = createCollegeDetail();
//        String collegeUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .body(collegeDetail)
//                .post(college)
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(collegeUrl, notNullValue());
//        String collegeUuid = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
//        assertThat(collegeUuid, matchesRegex(UUID_REGEX));
//
//        final DepartmentInput departmentInput = new DepartmentInput();
//        departmentInput.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
//        departmentInput.setDepartmentCode(RandomStringUtils.randomAlphanumeric(10));
//        departmentInput.setCollegeId(collegeUuid);
//        String departmentUrl = given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .body(departmentInput)
//                .post()
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertThat(departmentUrl, notNullValue());
//        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
//        assertThat(departmentUuid, matchesRegex(UUID_REGEX));
//
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("departmentId", is(equalTo(departmentUuid)))
//                .extract().as(DepartmentDetail.class);
//
//        given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .when().delete(departmentUuid)
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//
//        given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .when().get(departmentUuid)
//                .then()
//                .statusCode(NOT_FOUND.getStatusCode());
//
//        given()
//                .contentType(ContentType.JSON)
//                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
//                .when().get(college + "/{collegeId}", collegeUuid)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body(containsString(collegeUuid));
//    }
//
//
//    @Test
//    void deleteDepartmentFailsInvalidId() {
//        final String departmentUuid = UUID.randomUUID().toString();
//        ErrorResponse errorResponse = given()
//                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
//                .contentType(ContentType.JSON)
//                .when().delete(departmentUuid)
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode())
//                .extract().as(ErrorResponse.class);
//
//        assertThat(errorResponse.getErrorId(), is(nullValue()));
//        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
//        assertThat(errorResponse.getErrors(),
//                contains(new ErrorResponse.ErrorMessage(String.format("No department found for departmentId[%s]", departmentUuid)))
//        );
//
//    }

    private CollegeDetail createCollegeDetail() {
        CollegeDetail collegeDetail = new CollegeDetail();
        collegeDetail.setCollegeName(RandomStringUtils.randomAlphabetic(64));
        collegeDetail.setCollegeCode(RandomStringUtils.randomAlphabetic(10));
        collegeDetail.setStreet(RandomStringUtils.randomAlphabetic(32));
        collegeDetail.setWard(RandomStringUtils.randomAlphabetic(32));
        collegeDetail.setPostalCode(RandomStringUtils.randomNumeric(5));
        collegeDetail.setDistrict(RandomStringUtils.randomAlphabetic(32));
        collegeDetail.setCity(RandomStringUtils.randomAlphabetic(32));
        collegeDetail.setCountry(RandomStringUtils.randomAlphabetic(32));
        return collegeDetail;
    }

    private DepartmentInput createDepartment() {
        DepartmentInput departmentInput = new DepartmentInput();
        departmentInput.setDepartmentName("Department of Electronics and Telecommunication Engineering");
        departmentInput.setDepartmentCode("ETE");
        departmentInput.setCollegeId(null);
        return departmentInput;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}