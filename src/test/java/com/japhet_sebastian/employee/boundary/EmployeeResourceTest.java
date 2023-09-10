package com.japhet_sebastian.employee.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.employee.EmployeeResource;
import com.japhet_sebastian.employee.Employee;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.boundary.CollegeResource;
import com.japhet_sebastian.organization.boundary.DepartmentResource;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.EmploymentStatus;
import com.japhet_sebastian.vo.Gender;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(EmployeeResource.class)
@QuarkusTestResource(KeycloakResource.class)
//@QuarkusTestResource(WireMockQRGeneratorServiceProxy.class)
class EmployeeResourceTest extends AccessTokenProvider {

    @TestHTTPResource
    @TestHTTPEndpoint(CollegeResource.class)
    String college;

    @TestHTTPResource
    @TestHTTPEndpoint(DepartmentResource.class)
    String department;

    @TestHTTPResource("report")
    @TestHTTPEndpoint(EmployeeResource.class)
    String report;

    @TestHTTPResource("select")
    @TestHTTPEndpoint(EmployeeResource.class)
    String select;

    @Test
    void shouldGetAllEmployees() {
        List<Employee> employees = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response().jsonPath().getList("$");

        assertThat(employees, is(not(empty())));
        assertThat(employees, hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldGetPagesEmployeesList() {
        Response response = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<Employee> employees = response.jsonPath().getList("$");
        assertThat(employees, is(not(empty())));
        assertThat(employees, hasSize(greaterThanOrEqualTo(1)));
        assertThat(Integer.valueOf(response.getHeader("X-Total-Count")), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void employeesReport() {
        List<Employee> employees = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get(report + "?start=2023-03-01&end=2023-03-30")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().as(employeesReportTypeRef());

        assertThat(employees, is(not(empty())));
        assertThat(employees, hasSize(2));
    }

    @Test
    void getFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .when().get(select)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(greaterThanOrEqualTo(2)))
                .body(
                        containsString("hellen M. John"),
                        containsString("Michael J. Mbaga")
                );
    }

    @Test
    void getEmployeeById() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Get employee by UUID
        Employee employeeFound = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet"),
                        containsString("14110"),
                        containsString("UDSM2013-00005"),
                        containsString("Finance"),
                        containsString("lulu.shaban")
                )
                .extract().as(Employee.class);

        Integer yearsOfExperience = Period.between(employee.getHireDate(), LocalDate.now()).getYears();
        assertThat(employeeFound, is(notNullValue()));
        assertThat(employeeFound.getTimeOfService(), is(String.valueOf(yearsOfExperience)));
        assertThat(employeeUUID, equalTo(employeeFound.getEmployeeId()));
    }

    @Test
    void getForbidGetEmployeeById() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee by id");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee by Id");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet Forbidden");
        employee.setLastName("Sebastian Forbidden");
        employee.setEmail("japhet@gmail.com");
        employee.setMobile("0744608511");
        employee.setGender(Gender.M);
        employee.setWorkId("UDSM2013-00006");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Forbid an authorized user to access employee details
        given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(employeeUUID)
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void shouldGetEmployeeNotFound() {
        final String randomUUIDString = UUID.randomUUID().toString();
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get(randomUUIDString)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void saveEmployee() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee saved");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee saved");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet Saved");
        employee.setLastName("Sebastian Saved");
        employee.setEmail("japhets@gmail.com");
        employee.setMobile("0744608512");
        employee.setWorkId("UDSM2013-00007");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet Saved"),
                        containsString("Sebastian Saved"),
                        containsString("japhets@gmail.com"),
                        containsString("0744608512"),
                        containsString("UDSM2013-00007")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));
    }

    @Test
    void saveEmployeeFailsMissingAllRequiredFields() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee missing required");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee missing required");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = new Employee();
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(11)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("createEmployee.employee.firstName", getErrorMessage("Employee.firstName.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.lastName", getErrorMessage("Employee.lastName.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.country", getErrorMessage("Address.field.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.city", getErrorMessage("Address.field.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.dateOfBirth", getErrorMessage("Employee.dob.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.email", getErrorMessage("Email.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.status", getErrorMessage("Employee.status.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.street", getErrorMessage("Address.field.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.district", getErrorMessage("Address.field.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.mobile", getErrorMessage("Phone.number.required")),
                        new ErrorResponse.ErrorMessage("createEmployee.employee.hireDate", getErrorMessage("Employee.hire-date.required"))
                )
        );
    }

    @Test
    void saveEmployeeFailsEmployeeExists() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee exists");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee exists");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet exists");
        employee.setLastName("Sebastian exists");
        employee.setEmail("japhetseba@gmail.com");
        employee.setMobile("0744608510");
        employee.setWorkId("UDSM2013-00007");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), notNullValue());
        assertThat(errorResponse.getErrors(), contains(new ErrorResponse.ErrorMessage("Employee exists")));
    }

    @Test
    void saveEmployeeFailsDepartmentNotFound() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee department not found");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // init department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee department not found");

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet exists");
        employee.setLastName("Sebastian exists");
        employee.setEmail("japhetseba@gmail.com");
        employee.setMobile("0744608510");
        employee.setWorkId("UDSM2013-00007");
        employee.setDepartmentName(departmentInput.getDepartmentName());
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), notNullValue());
        assertThat(errorResponse.getErrors(), contains(new ErrorResponse.ErrorMessage("Could not find department for associated employee")));
    }

    @Test
    void updateOnlyEmployee() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee updated");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee updated");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet before update");
        employee.setLastName("Sebastian before update");
        employee.setEmail("japhet.seba@gmail.com");
        employee.setMobile("0744608519");
        employee.setWorkId("UDSM2013-00008");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet before update"),
                        containsString("Sebastian before update"),
                        containsString("japhet.seba@gmail.com"),
                        containsString("0744608519"),
                        containsString("UDSM2013-00008")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // update employee name
        employeeSaved.setFirstName("Japhet updated");
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm it is updated
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet updated")
                );
    }

    @Test
    void updateEmployeeAddress() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee address updated");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee address updated");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet address update");
        employee.setLastName("Sebastian address update");
        employee.setEmail("japhet.sebastian@gmail.com");
        employee.setMobile("0744608520");
        employee.setWorkId("UDSM2013-00009");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet address update"),
                        containsString("Sebastian address update"),
                        containsString("japhet.sebastian@gmail.com"),
                        containsString("0744608520"),
                        containsString("UDSM2013-00009")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // update employee name
        employeeSaved.setLastName("Sebastian updated");
        employeeSaved.setStreet("Saranga street updated");
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm it is updated
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Sebastian updated"),
                        containsString("Saranga street updated")
                );
    }

    @Test
    void updateEmployeeDepartment() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee department");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee department");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet department");
        employee.setLastName("Sebastian department");
        employee.setEmail("japhet.department@gmail.com");
        employee.setMobile("0744608522");
        employee.setWorkId("UDSM2013-00010");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet department"),
                        containsString("Sebastian department"),
                        containsString("japhet.department@gmail.com"),
                        containsString("0744608522"),
                        containsString("UDSM2013-00010")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));


        // create department to update
        final DepartmentInput departmentUpdated = createDepartment();
        departmentUpdated.setDepartmentName("Department updated");
        departmentUpdated.setCollegeId(collegeUUID);
        String departmentUpdatedURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentUpdated)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentUpdatedURL, notNullValue());
        String departmentUpdatedUUID = departmentUpdatedURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUpdatedUUID, matchesRegex(UUID_REGEX));

        // Get the updated department
        DepartmentDetail departmentUpdatedDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUpdatedUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUpdatedUUID)))
                .extract().as(DepartmentDetail.class);

        // update employee department
        employeeSaved.setDepartmentName(departmentUpdatedDetail.getDepartmentName());
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm it is updated
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Department updated")
                );
    }

    @Test
    void updateFailsEmployeeEmptyStringId() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee empty ID");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee empty ID");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet empty ID");
        employee.setLastName("Sebastian empty ID");
        employee.setEmail("japhet.empty@gmail.com");
        employee.setMobile("0744608523");
        employee.setWorkId("UDSM2013-00011");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet empty ID"),
                        containsString("Sebastian empty ID"),
                        containsString("japhet.empty@gmail.com"),
                        containsString("0744608523"),
                        containsString("UDSM2013-00011")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // fail to update employee with empty ID
        employeeSaved.setEmployeeId("");
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Employee does not have employeeId"))
        );
    }

    @Test
    void updateFailsEmployeeNullId() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee null ID");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee null ID");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet null ID");
        employee.setLastName("Sebastian null ID");
        employee.setEmail("japhet.null@gmail.com");
        employee.setMobile("0744608524");
        employee.setWorkId("UDSM2013-00012");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet null ID"),
                        containsString("Sebastian null ID"),
                        containsString("japhet.null@gmail.com"),
                        containsString("0744608524"),
                        containsString("UDSM2013-00012")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // fail to update employee with empty ID
        employeeSaved.setEmployeeId(null);
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Employee does not have employeeId"))
        );
    }

    @Test
    void updateFailsEmployeeIdMismatch() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee ID mismatch");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee ID mismatch");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet ID mismatch");
        employee.setLastName("Sebastian ID mismatch");
        employee.setEmail("japhet.mismatch@gmail.com");
        employee.setMobile("0744608525");
        employee.setWorkId("UDSM2013-00013");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet ID mismatch"),
                        containsString("Sebastian ID mismatch"),
                        containsString("japhet.mismatch@gmail.com"),
                        containsString("0744608525"),
                        containsString("UDSM2013-00013")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // fail to update employee with empty ID
        employeeSaved.setEmployeeId(RandomStringUtils.randomAlphabetic(15));
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employeeSaved)
                .when().put(employeeUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("path variable employeeId does not match Employee.employeeId"))
        );
    }

    @Test
    void updateFailsEmployeeDepartmentNotFound() {
        final Employee employee = createEmployee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName("Japhet not found");
        employee.setLastName("Sebastian not found");
        employee.setEmail("japhet.mismatch@gmail.com");
        employee.setMobile("0744608525");
        employee.setWorkId("UDSM2013-00013");
        employee.setDepartmentName(RandomStringUtils.randomAlphabetic(20));

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employee)
                .when().put(employee.getEmployeeId())
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Could not find department for associated employee"))
        );
    }

    @Test
    void updateFailsEmployeeNotFound() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee not found");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee not found");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName("Japhet not found");
        employee.setLastName("Sebastian not found");
        employee.setEmail("japhet.mismatch@gmail.com");
        employee.setMobile("0744608525");
        employee.setWorkId("UDSM2013-00013");
        employee.setDepartmentName(departmentDetail.getDepartmentName());

        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .contentType(ContentType.JSON)
                .body(employee)
                .when().put(employee.getEmployeeId())
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(String.format("No employee found for employeeId[%s]", employee.getEmployeeId())))
        );
    }

    @Test
    void deleteEmployeeFailsNotFound() {
        String employeeUUID = UUID.randomUUID().toString();
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().delete(employeeUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(String.format("Could not find employee for employeeId[%s]", employeeUUID)))
        );
    }

    @Test
    void deleteEmployee() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create college
        final CollegeDetail collegeDetail = createCollegeDetail();
        collegeDetail.setCollegeName("Employee deleted");
        String collegeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(collegeDetail).post(college)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(collegeURL, notNullValue());
        String collegeUUID = collegeURL.substring(collegeURL.lastIndexOf("/") + 1);
        assertThat(collegeUUID, matchesRegex(UUID_REGEX));

        // create department
        final DepartmentInput departmentInput = createDepartment();
        departmentInput.setDepartmentName("Employee deleted");
        departmentInput.setCollegeId(collegeUUID);
        String departmentURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(departmentInput)
                .post(department)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(departmentURL, notNullValue());
        String departmentUUID = departmentURL.substring(departmentURL.lastIndexOf("/") + 1);
        assertThat(departmentUUID, matchesRegex(UUID_REGEX));

        // Get the created department
        DepartmentDetail departmentDetail = given()
                .header(ACCEPT, APPLICATION_JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(department + "/{departmentId}", departmentUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("departmentId", is(equalTo(departmentUUID)))
                .extract().as(DepartmentDetail.class);

        // add employee
        final Employee employee = createEmployee();
        employee.setFirstName("Japhet deleted");
        employee.setLastName("Sebastian deleted");
        employee.setEmail("japhet.deleted@gmail.com");
        employee.setMobile("0744608533");
        employee.setWorkId("UDSM2013-00077");
        employee.setDepartmentName(departmentDetail.getDepartmentName());
        String employeeURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(employee).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(employeeURL, notNullValue());
        String employeeUUID = employeeURL.substring(employeeURL.lastIndexOf("/") + 1);
        assertThat(employeeUUID, matchesRegex(UUID_REGEX));

        // Check created employee
        Employee employeeSaved = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Japhet deleted"),
                        containsString("Sebastian deleted"),
                        containsString("japhet.deleted@gmail.com"),
                        containsString("0744608533"),
                        containsString("UDSM2013-00077")
                )
                .extract().as(Employee.class);

        assertThat(employeeSaved, is(notNullValue()));
        assertThat(employeeSaved.getEmployeeId(), equalTo(employeeUUID));
        assertThat(employeeSaved.getDepartmentName(), equalTo(departmentDetail.getDepartmentName()));

        // delete employee
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().delete(employeeUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // confirm deletion
        given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .when().get(employeeUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    private TypeRef<List<Employee>> employeesReportTypeRef() {
        return new TypeRef<List<Employee>>() {
        };
    }

    private TypeRef<Employee> getEmployeeTypeRef() {
        return new TypeRef<Employee>() {
        };
    }

    private Employee createEmployee() {
        final Employee employee = new Employee();
        employee.setFirstName("Japhet");
        employee.setLastName("Sebastian");
        employee.setEmail("japhetseba@gmail.com");
        employee.setMobile("0744608510");
        employee.setGender(Gender.M);
        employee.setWorkId("UDSM2013-00005");
        employee.setDateOfBirth(LocalDate.of(1992, Month.JUNE, 12));
        employee.setHireDate(LocalDate.of(2019, Month.APRIL, 8));
        employee.setStatus(EnumSet.of(EmploymentStatus.CONTRACT, EmploymentStatus.FULL_TIME));
        employee.setStreet("Mikocheni");
        employee.setPostalCode("14110");
        employee.setDistrict("Kinondoni");
        employee.setCity("Dar es Salaam");
        employee.setCountry("Tanzania");
        return employee;
    }

    private CollegeDetail createCollegeDetail() {
        CollegeDetail collegeDetail = new CollegeDetail();
        collegeDetail.setCollegeName("College of Humanity");
        collegeDetail.setCollegeCode("CoHu");
        collegeDetail.setStreet("Chuo kikuu");
        collegeDetail.setPostalCode("15114");
        collegeDetail.setDistrict("Ubungo");
        collegeDetail.setCity("Dar es salaam");
        collegeDetail.setCountry("Tanzania");
        return collegeDetail;
    }

    private DepartmentInput createDepartment() {
        final DepartmentInput departmentInput = new DepartmentInput();
        departmentInput.setDepartmentName("Finance");
        departmentInput.setDepartmentCode("FIN012");
        departmentInput.setDescription("Finance department");
        departmentInput.setCollegeId(null);
        return departmentInput;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }

}