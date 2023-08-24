package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

import static com.japhet_sebastian.employee.EmployeeResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Employee Endpoint", description = "Employees management operations")
public class EmployeeResource {

    public static final String RESOURCE_PATH = "/employees";

    @Inject
    Logger LOGGER;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    SecurityIdentity keycloakSecurityContext;

    @Inject
    EmployeeService employeeService;

    @GET
    @Operation(summary = "Get all available employees")
    @APIResponse(
            responseCode = "200",
            description = "Lists all the employees",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmployeeDetail.class, type = SchemaType.ARRAY)))
    public Response listEmployees(@BeanParam PageRequest pageRequest) {
        LOGGER.info("ID TOKEN : " + idToken.getName());
        LOGGER.info("KEYCLOAK : " + keycloakSecurityContext.getPrincipal().getName());
        List<EmployeeDetail> employeeDetails = employeeService.listEmployees(pageRequest);
        Long totalCount = this.employeeService.totalEmployees();
        return Response.ok(employeeDetails).header("X-Total-Count", totalCount).build();
    }

//    @GET
//    @Path("report")
//    @Operation(summary = "Retrieves a specified range of employees record for generating report")
//    @APIResponses({
//            @APIResponse(responseCode = "200",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                            schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
//                    description = "Generate employees report"),
//            @APIResponse(responseCode = "204", description = "No data for report"),
//    })
//    public Response unPaginatedList(
//            @Parameter(description = "Search date", required = false) @QueryParam("start") LocalDate startDate,
//            @Parameter(description = "Search endDate", required = false) @QueryParam("end") LocalDate endDate
//    ) {
//
//        List<Employee> employees = employeeService.unPaginatedList(startDate, endDate);
//        if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
//        return Response.ok(employees).build();
//
//    }
//
//    @GET
//    @Path("/{id}")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Returns the employee for a given identifier")
//    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Returns a found employee"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee is not found for a given identifier")})
//    public Response findEmployee(@Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long empId) {
//        return employeeService.findById(empId).map(employee -> Response.ok(employee).build()).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
//    }
//
//    @GET
//    @Path("/select")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Retrieve only employee ID and first name from all employees in the database")
//    @APIResponses({
//            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
//                    description = "ID and first name for all employees available"),
//            @APIResponse(responseCode = "204", description = "No employee available in the database")
//    })
//    public Response employeesSelectOptions() {
//        List<SelectOptions> employees = Employee.find("SELECT e.id, e.firstName || ' ' || CONCAT(SUBSTRING(e.middleName, 1, 1), '.') || ' ' || e.lastName FROM Employee e")
//                .project(SelectOptions.class).list();
//        if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
//
//        return Response.ok(employees).build();
//    }
//
//    @POST
//    @Operation(summary = "Creates a valid employee and stores it into the database")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "201",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
//                    description = "URI of the created employee"),
//            @APIResponse(responseCode = "400", description = "Invalid input"),
//            @APIResponse(
//                    responseCode = "409",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
//                    description = "Employee duplications is not allowed"),
//            @APIResponse(
//                    responseCode = "404",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
//                    description = "Specified department does not exist in the database")})
//    public Response createEmployee(
//            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                    schema = @Schema(implementation = Employee.class))) @Valid Employee employee,
//            @Context UriInfo uriInfo) {
//        if (employee.address == null || employee.department == null) return Response.status(Status.BAD_REQUEST).build();
//        else if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
//            if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
//                return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
//            else if (!Department.findByIdOptional(employee.department.id).isPresent())
//                return Response.status(Status.NOT_FOUND).entity("Department dont exists").build();
//
//        employee.registeredBy = keycloakSecurityContext.getPrincipal().getName();
//        employee = employeeService.addEmployee(employee);
//        URI employeeUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(employee.id)).build();
//        return Response.created(employeeUri).build();
//    }
//
//    @PUT
//    @Path("/{id}")
//    @Operation(summary = "Updates an existing employee")
//    @APIResponses({@APIResponse(responseCode = "204", description = "Employee has been successfully updated"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee to be updated does not exist in the database"), @APIResponse(responseCode = "415", description = "Format is not JSON"), @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Employee payload is not the same as an entity object that needed to be updated")})
//    public Response updateEmployee(@Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId, @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class))) @Valid Employee employee) {
//        if (!empId.equals(employee.id)) return Response.status(Response.Status.CONFLICT).entity(employee).build();
//        else if (employee.department == null) return Response.status(Status.BAD_REQUEST).build();
//
//        employee.address = null;
//        employee.updatedBy = keycloakSecurityContext.getPrincipal().getName();
//        try {
//            employeeService.updateEmployee(employee, empId);
//        } catch (NotFoundException nf) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        return Response.status(Status.NO_CONTENT).build();
//    }
//
//    @DELETE
//    @Path("/{id}")
//    @Operation(summary = "Deletes an existing employee")
//    @APIResponses({
//            @APIResponse(responseCode = "204", description = "Employee has been successfully deleted"),
//            @APIResponse(responseCode = "400", description = "Invalid input"),
//            @APIResponse(responseCode = "404", description = "Employee to be deleted does not exist in the database"),
//            @APIResponse(responseCode = "500", description = "Employee not found")})
//    public Response deleteEmployee(
//            @Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId
//    ) {
//        try {
//            employeeService.deleteEmployee(empId);
//        } catch (EntityNotFoundException nfe) {
//            return Response.status(Status.NOT_FOUND).build();
//        }
//        return Response.noContent().build();
//    }
}
