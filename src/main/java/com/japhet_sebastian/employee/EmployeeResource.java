package com.japhet_sebastian.employee;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.Objects;

import static com.japhet_sebastian.employee.EmployeeResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Employee Endpoint", description = "Employees management operations")
public class EmployeeResource extends AbstractEmployeeType {

    public static final String RESOURCE_PATH = "/employees";

    @Inject
    Logger LOGGER;

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
                    schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)))
    public Response listEmployees(@BeanParam PageRequest pageRequest) {
        return Response.ok(this.employeeService.listEmployees(pageRequest))
                .header("X-Total-Count", this.employeeService.totalEmployees())
                .build();
    }

    @GET
    @Path("report")
    @Operation(summary = "Generate report for a specified range of employees record")
    @APIResponse(
            responseCode = "200",
            description = "Generate employees report",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)))
    public Response getReport(@BeanParam ReportPage reportPage) {
        return Response
                .ok(this.employeeService.departmentsReport(reportPage.getStartDate(), reportPage.getEndDate()))
                .build();
    }

    @GET
    @Path("/{employeeId}")
    @Operation(summary = "Get employee for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get a found employee object",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Employee.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "Employee does not exist for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response findEmployee(
            @Parameter(description = "employeeId", required = true)
            @PathParam("employeeId") @NotNull String employeeId) {
        return employeeService.getEmployee(employeeId)
                .map(employee -> Response.ok(employee).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

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

    @POST
    @Operation(summary = "Creates valid employee")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Employee created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class, type = SchemaType.STRING))),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Could not find department for associated employee",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response createEmployee(@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Employee.class))) @Valid Employee employee, @Context UriInfo uriInfo) {
        employee.setRegisteredBy(keycloakSecurityContext.getPrincipal().getName());
        this.employeeService.addEmployee(employee);
        URI employeeUri = employeeUriBuilder(employee.getEmployeeId(), uriInfo).build();
        return Response.created(employeeUri).build();
    }

    @PUT
    @Path("/{employeeId}")
    @Operation(summary = "Updates existing employee")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Employee updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Employee.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "No employee found for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid object",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Employee object does not have employeeId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Path variable employeeId does not match Employee.employeeId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
    })
    public Response updateEmployee(
            @Parameter(description = "employeeId", required = true) @PathParam("employeeId") @NotNull String employeeId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Employee.class))) @Valid Employee employee) {
        if (Objects.isNull(employee.getEmployeeId()) || employee.getEmployeeId().isEmpty())
            throw new ServiceException("Employee does not have employeeId");

        if (!Objects.equals(employeeId, employee.getEmployeeId()))
            throw new ServiceException("path variable employeeId does not match Employee.employeeId");

        employee.setUpdatedBy(keycloakSecurityContext.getPrincipal().getName());
        employeeService.updateEmployee(employee);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

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
