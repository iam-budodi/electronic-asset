package com.japhet_sebastian.employee.boundary;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.employee.entity.Employee;
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

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.EnumSet;
import java.util.List;
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
        assertThat(response.getHeader("X-Total-Count"), is(greaterThanOrEqualTo(String.valueOf(2))));
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
                .body("label", containsInAnyOrder("hellen M. John", "Michael J. Mbaga"));
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

//    @Test
//    @Order(3)
//    void shouldNotCreateInvalidEmployee() {
//        final Employee employee = new Employee();
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//    }
//
//    @Test
//    @Order(4)
//    void ShouldCreateDepartment() {
//        final Department dept = new Department();
//        dept.departmentName = DEPARTMENT_NAME;
//        dept.description = DEPARTMENT_DESCRIPTION;
//
//        // creates department
//        String deptLocation = given()
//                .body(dept)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post(deptUrl)
//                .then()
//                .statusCode(Status.CREATED.getStatusCode())
////					.header("location", deptUrl + "/2") // leave for testing failure results
//                .extract().response().getHeader("Location");
//
//        // retrieve created URI and extract department Id
//        String[] elements = deptLocation.split("/");
//        departmentId = elements[elements.length - 1];
//        assertThat(departmentId).isNotNull();
//    }
//
//    @Test
//    @Order(5)
//    void shouldFetchDepartment() {
//        // fetch created department
//        department = given()
//                .accept(ContentType.JSON)
//                .pathParam("id", departmentId)
//                .when()
//                .get(deptUrl + "/{id}")
//                .then()
//                .statusCode(OK.getStatusCode())
//                .contentType(ContentType.JSON)
////					.extract().body().as(getDepartmentTypeRef());
//                .extract().as(Department.class);
//
//        assertThat(department).isNotNull();
//    }
//
//    @Test
//    @Order(6)
//    void shouldCreateEmployee() {
//        // Creating Address object
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = DEFAULT_FNAME;
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = DEFAULT_EMAIL;
//        employee.mobile = DEFAULT_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = DEFAULT_WORK_ID;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.address = address;
//        employee.department = department;
//
//        String location = given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(Status.CREATED.getStatusCode())
//                .extract().response().getHeader("Location");
//
//        assertTrue(location.contains("employees"));
//        String[] segments = location.split("/");
//        employeeId = segments[segments.length - 1];
//        assertNotNull(employeeId);
//
//    }
//
//    @Test
//    @Order(7)
//    void shouldFindEmployees() {
//        List<Employee> employees = given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .get()
//                .then()
//                .statusCode(OK.getStatusCode())
//                .body("", is(not(empty())))
//                .contentType(APPLICATION_JSON)
//                .extract().body().as(getEmployeesTypeRef());
//
//        assertTrue(employees.size() >= 1);
//        assertThat(employees.size(), greaterThanOrEqualTo(1));
//    }
//
//    @Test
//    @Order(8)
//    void shouldFindEmployee() {
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", employeeId)
//                .when()
//                .get("/{id}")
//                .then()
//                .statusCode(OK.getStatusCode())
//                .contentType(APPLICATION_JSON)
//                .body("firstName", is(DEFAULT_FNAME))
//                .body("lastName", is(DEFAULT_LNAME))
//                .body("email", is(DEFAULT_EMAIL))
//                .body("mobile", is(DEFAULT_PHONE))
//                .body("hireDate", is(DEFAULT_HIRE_DATE.toString()))
//                .body("status", hasItems("CONTRACT", "FULL_TIME"))
//                .body("registeredBy", is(DEFAULT_REGISTERED_BY))
//                .body("department.id", is(Integer.valueOf(departmentId)))
//                .body("address.id", is(notNullValue()))
//                .body("address.street", is(STREET))
//                .body("address.city", is(CITY))
//                .body("address.postalCode", is(POSTAL_CODE));
//    }
//
//    @Test
//    @Order(9)
//    void shouldCountEmployee() {
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .get(countEndpoint)
//                .then()
//                .statusCode(OK.getStatusCode())
//                .contentType(APPLICATION_JSON)
////				.body(containsString(String.valueOf(count)))
//                .body(is(greaterThanOrEqualTo(String.valueOf(1))));
//    }
//
//    @Test
//    @Order(10)
//    void shouldFailToUpdateRandomEmployee() {
//        final Long randomId = new Random().nextLong();
//
//        final Employee employee = new Employee();
//        employee.id = Long.valueOf(employeeId);
//        employee.firstName = UPDATED_FNAME;
//        employee.lastName = UPDATED_LNAME;
//        employee.email = UPDATED_EMAIL;
//        employee.mobile = UPDATED_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = "UDSM-2023-0012";
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", randomId)
//                .when()
//                .put("/{id}")
//                .then()
//                .statusCode(CONFLICT.getStatusCode());
//    }
//
//    @Test
//    @Order(11)
//    void shouldFailToUpdateEmployeeWithoutDepartment() {
//        final Long randomId = new Random().nextLong();
//
//        final Employee employee = new Employee();
//        employee.id = randomId;
//        employee.firstName = UPDATED_FNAME;
//        employee.lastName = UPDATED_LNAME;
//        employee.email = UPDATED_EMAIL;
//        employee.mobile = UPDATED_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = "UDSM-2023-0012";
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", randomId)
//                .when()
//                .put("/{id}")
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//    }
//
//    @Test
//    @Order(12)
//    void shouldFailToUpdateUnknownEmployee() {
//        final Long randomId = new Random().nextLong();
//
//        final Employee employee = new Employee();
//        employee.id = randomId;
//        employee.firstName = UPDATED_FNAME;
//        employee.lastName = UPDATED_LNAME;
//        employee.email = UPDATED_EMAIL;
//        employee.mobile = UPDATED_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = "UDSM-2023-0012";
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", randomId)
//                .when()
//                .put("/{id}")
//                .then()
//                .statusCode(NOT_FOUND.getStatusCode());
//    }
//
//    @Test
//    @Order(13)
//    void shouldNotUpdateAddressWhileUpdatingEmployee() {
//        // Creating Address object
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = "15000";
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.id = Long.valueOf(employeeId);
//        employee.firstName = UPDATED_FNAME;
//        employee.lastName = UPDATED_LNAME;
//        employee.email = UPDATED_EMAIL;
//        employee.mobile = UPDATED_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = "UDSM-2023-0012";
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.department = department;
//        employee.address = address;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", employeeId)
//                .when()
//                .put("/{id}")
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//    }
//
//    @Test
//    @Order(14)
//    void shouldCheckAddressIsNotUpdated() {
//        Employee employee = given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", employeeId)
//                .when()
//                .get("/{id}")
//                .then()
//                .statusCode(OK.getStatusCode())
//                .contentType(APPLICATION_JSON)
//                .extract().body().as(getEmployeeTypeRef());
//
//        assertThat(employee.address.postalCode, not(equalTo("15000")));
//        assertThat(employee.address.postalCode, is(equalTo(POSTAL_CODE)));
//    }
//
//    @Test
//    @Order(15)
//    void shouldUpdateEmployee() {
//        final Employee employee = new Employee();
//        employee.id = Long.valueOf(employeeId);
//        employee.firstName = UPDATED_FNAME;
//        employee.lastName = UPDATED_LNAME;
//        employee.email = DEFAULT_EMAIL;
//        employee.mobile = "+(255) 744 608 510";
//        employee.gender = DEFAULT_GENDER;
//        employee.workId = "UDSM-2023-0002";
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", employeeId)
//                .when()
//                .put("/{id}")
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//    }
//
//    @Test
//    @Order(16)
//    void shouldNotDeleteUnknownEmployee() {
//        Long randomId = new Random().nextLong();
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", randomId)
//                .when()
//                .delete("/{id}")
//                .then()
//                .statusCode(NOT_FOUND.getStatusCode());
//    }
//
//    @Test
//    @Order(17)
//    void shouldDeleteEmployee() {
//        given()
//                .header(ACCEPT, APPLICATION_JSON)
//                .pathParam("id", employeeId)
//                .when()
//                .delete("/{id}")
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//    }
//
//    @Test
//    @Order(18)
//    void shouldFailDeleteUnexistingEmployeeFromDepartment() {
//        // delete associated employee from employees table
//        given()
//                .accept(ContentType.JSON)
//                .pathParam("id", departmentId)
//                .queryParam("empid", employeeId)
//                .when()
//                .delete(deptUrl + "/{id}/employee")
//                .then()
//                .statusCode(NOT_FOUND.getStatusCode());
//    }
//
//    @Test
//    @Order(19)
//    void shouldResetDepartment() {
//        // reset department table
//        given()
//                .accept(ContentType.JSON)
//                .pathParam("id", departmentId)
//                .when()
//                .delete(deptUrl + "/{id}")
//                .then()
//                .statusCode(NO_CONTENT.getStatusCode());
//    }
//
//    @Test
//    @Order(20)
//    void shouldNotCreateEmployeeWhenSupplyingNullValuesForRequiredFields() {
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = DEFAULT_FNAME;
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = null;
//        employee.mobile = DEFAULT_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.address = null;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//
//    }
//
//    @Test
//    @Order(21)
//    void shouldNotCreateEmployeeWhenInvalidCharatersSuppliedOnNamesFields() {
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = "J@ph3t";
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = DEFAULT_EMAIL;
//        employee.mobile = DEFAULT_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.address = address;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//
//    }
//
//    @Test
//    @Order(22)
//    void shouldNotCreateEmployeeWhenInvalidCharateruppliedOnEmailField() {
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = DEFAULT_FNAME;
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = "j@phetseba@gmail.com";
//        employee.mobile = DEFAULT_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.address = address;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//
//    }
//
//    @Test
//    @Order(23)
//    void shouldNotCreateEmployeeWhenInvalidCharaterSuppliedOnMobileNumberField() {
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = DEFAULT_FNAME;
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = DEFAULT_EMAIL;
//        employee.mobile = "(255)744.111.789";
//        employee.gender = DEFAULT_GENDER;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.address = address;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//
//    }
//
//    @Test
//    @Order(24)
//    void shouldNotCreateEmployeeWithoutAddress() {
//        final Address address = new Address();
//        address.street = STREET;
//        address.ward = WARD;
//        address.district = DISTRICT;
//        address.city = CITY;
//        address.postalCode = POSTAL_CODE;
//        address.country = COUNTRY;
//
//        final Employee employee = new Employee();
//        employee.firstName = DEFAULT_FNAME;
//        employee.lastName = DEFAULT_LNAME;
//        employee.email = DEFAULT_EMAIL;
//        employee.mobile = DEFAULT_PHONE;
//        employee.gender = DEFAULT_GENDER;
//        employee.dateOfBirth = DEFAULT_BIRTH_DATE;
//        employee.hireDate = DEFAULT_HIRE_DATE;
//        employee.status = DEFAULT_STATUS;
//        employee.registeredBy = DEFAULT_REGISTERED_BY;
//        employee.department = department;
//
//        given()
//                .body(employee)
//                .header(CONTENT_TYPE, APPLICATION_JSON)
//                .header(ACCEPT, APPLICATION_JSON)
//                .when()
//                .post()
//                .then()
//                .statusCode(BAD_REQUEST.getStatusCode());
//    }

    private TypeRef<List<Employee>> getEmployeesTypeRef() {
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


//        private static final String UPDATED_FNAME = "Japhet - updated";
//        private static final String UPDATED_LNAME = "Sebastian - updated";
//        private static final String UPDATED_EMAIL = "luluyshaban@gmail.com";
//        private static final String UPDATED_PHONE = "+(255)-716-656-596";

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
}