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
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
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
        String totalItem = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .body("$", is(not(empty())))
                .body("size()", is(greaterThanOrEqualTo(2)))
                .body(
                        containsString("TE"),
                        containsString("CSE")
                ).extract().response().getHeader("X-Total-Count");

        assertThat(Integer.valueOf(totalItem), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldGetPagedList() {
        String totalItem = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(1))
                .body("departmentCode",
                        either(contains("CSE")).or(contains("Random")).or(contains("ETE"))
                                .or(contains("FIN012")).or(contains("KNOWN"))
                ).extract().response().getHeader("X-Total-Count");

        assertThat(Integer.valueOf(totalItem), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldGetFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(selectPath)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$", hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldGetDepartmentById() {
        // create college
        CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUID = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        DepartmentDto department = createDepartment();
        department.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        department.setDepartmentCode("Random");
        department.setCollege(collegeCreated);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(department).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUuid, matchesRegex(UUID_REGEX));

        // validate the created department
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUuid)))
                .body("departmentName", is(notNullValue()))
                .body("departmentCode", is(equalTo("Random")));
    }

    @Test
    void shouldNotFindDepartment() {
        final String randomUuidString = UUID.randomUUID().toString();
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(randomUuidString)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldSaveDepartment() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUID = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // save department
        final DepartmentDto departmentBody = createDepartment();
        departmentBody.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        departmentBody.setDepartmentCode("KNOWN");
        departmentBody.setCollege(collegeCreated);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentBody).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUuid, matchesRegex(UUID_REGEX));

        // validate the created department
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUuid)));
    }

    @Test
    void shouldFailOnSaveNoDepartmentName() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUID = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // save and validate department
        final DepartmentDto departmentBody = createDepartment();
        departmentBody.setDepartmentName(null);
        departmentBody.setCollege(collegeCreated);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentBody).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("saveDepartment.departmentDto.departmentName",
                        getErrorMessage("Department.field.required"))));
    }

    @Test
    void shouldFailOnSaveNoCollegeId() {
        final DepartmentDto departmentBody = createDepartment();
        departmentBody.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        departmentBody.setCollege(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentBody).post()
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(notNullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(getErrorMessage("System.error"))));
    }

    @Test
    void shouldFailOnSaveViolatesUniqueConstraint() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUID = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        final DepartmentDto departmentDto = createDepartment();
        departmentDto.setCollege(collegeCreated);
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentDto)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        // duplicating department
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), hasSize(1));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Department with same name already exists")));
    }

    @Test
    void shouldFailOnSaveConstraintViolations() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUId = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUId, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        final DepartmentDto department = createDepartment();
        department.setDepartmentName(null);
        department.setDepartmentCode(RandomStringUtils.randomAlphanumeric(12));
        department.setCollege(collegeCreated);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(department).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(2)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                new ErrorResponse.ErrorMessage("saveDepartment.departmentDto.departmentName", getErrorMessage("Department.field.required")),
                new ErrorResponse.ErrorMessage("saveDepartment.departmentDto.departmentCode", getErrorMessage("Alphanumeric.character.length")))
        );
    }

    @Test
    void updateDepartment() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUId = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUId, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        final DepartmentDto department = new DepartmentDto();
        department.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        department.setDepartmentCode("KNOWN");
        department.setCollege(collegeCreated);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(department).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUuid, matchesRegex(UUID_REGEX));

        // get created department
        DepartmentDto departmentDtoSaved = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUuid)))
                .extract().as(DepartmentDto.class);

        // update department
        String departmentNameUpdated = "Dept of Electronics and Telecommunication Engineering - Update";
        department.setDepartmentId(departmentUuid);
        department.setCollege(collegeCreated);
        department.setDepartmentName(departmentNameUpdated);

        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(department)
                .when().put(departmentUuid)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // validate updated department
        DepartmentDto departmentDtoUpdated = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString(departmentNameUpdated),
                        containsString(collegeBody.getCollegeName())
                )
                .extract().as(DepartmentDto.class);

        assertThat(departmentNameUpdated, is(equalTo(departmentDtoUpdated.getDepartmentName())));
        assertThat(departmentDtoSaved.getDepartmentId(), is(equalTo(departmentDtoUpdated.getDepartmentId())));
        assertThat(departmentDtoSaved.getDepartmentName(), is(not(equalTo(departmentDtoUpdated.getDepartmentName()))));
        assertThat(departmentDtoSaved.getAddress(), is(equalTo(departmentDtoUpdated.getAddress())));
    }

    @Test
    void updateDepartmentFailsNoDepartmentName() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUId = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUId, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        final DepartmentDto department = new DepartmentDto();
        department.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        department.setDepartmentCode("KNOWN");
        department.setCollege(collegeCreated);
        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(department).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUuid, matchesRegex(UUID_REGEX));

        // update department
        department.setDepartmentId(departmentUuid);
        department.setDepartmentName(null);

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(department)
                .when().put(departmentUuid)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("updateDepartment.department.departmentName",
                        getErrorMessage("Department.field.required")))
        );
    }

    @Test
    void deleteDepartment() {
        // create college
        final CollegeDto collegeBody = createCollegeDto();
        String collegeUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(collegeBody).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeUrl, notNullValue());
        String collegeUUId = collegeUrl.substring(collegeUrl.lastIndexOf("/") + 1);
        assertThat(collegeUUId, matchesRegex(UUID_REGEX));

        // retrieve created college
        CollegeDto collegeCreated = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(CollegeDto.class);

        assertThat(collegeCreated, notNullValue());

        // create department
        final DepartmentDto department = new DepartmentDto();
        department.setDepartmentName(RandomStringUtils.randomAlphabetic(32));
        department.setDepartmentCode("KNOWN");
        department.setCollege(collegeCreated);

        String departmentUrl = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(department).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUrl, notNullValue());
        String departmentUuid = departmentUrl.substring(departmentUrl.lastIndexOf("/") + 1);
        assertThat(departmentUuid, matchesRegex(UUID_REGEX));

        // check created department
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUuid)));

        // delete department
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().delete(departmentUuid)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm deleted
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(departmentUuid)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        // confirm college is not deleted
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(college + "/{collegeId}", collegeUUId)
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString(collegeUUId));
    }

    @Test
    void deleteDepartmentFailsInvalidId() {
        final String departmentUUID = UUID.randomUUID().toString();
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().delete(departmentUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(String.format("No department found for departmentId[%s]", departmentUUID)))
        );

    }

    private CollegeDto createCollegeDto() {
        final AddressDto addressDto = new AddressDto();
        addressDto.setStreet(RandomStringUtils.randomAlphabetic(32));
        addressDto.setPostalCode(RandomStringUtils.randomNumeric(5));
        addressDto.setDistrict(RandomStringUtils.randomAlphabetic(32));
        addressDto.setCity(RandomStringUtils.randomAlphabetic(32));
        addressDto.setCountry(RandomStringUtils.randomAlphabetic(32));

        final CollegeDto collegeDto = new CollegeDto();
        collegeDto.setCollegeName(RandomStringUtils.randomAlphabetic(64));
        collegeDto.setCollegeCode(RandomStringUtils.randomAlphabetic(10));
        collegeDto.setAddress(addressDto);
        return collegeDto;
    }

    private DepartmentDto createDepartment() {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentName("Department of Electronics and Telecommunication Engineering");
        departmentDto.setDepartmentCode("ETE");
        departmentDto.setCollege(null);
        return departmentDto;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }
}