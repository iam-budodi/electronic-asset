package com.japhet_sebastian.employee;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.vo.SelectOptions;
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

import java.net.URI;
import java.util.Objects;

import static com.japhet_sebastian.employee.EmployeeResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Employee Endpoint", description = "Employees related operations")
public class EmployeeResource extends AbstractEmployeeType {

    public static final String RESOURCE_PATH = "/employees";

    @Inject
    SecurityIdentity keycloakSecurityContext;

    @Inject
    EmployeeService employeeService;

    @GET
    @Operation(summary = "Get all employees")
    @APIResponse(
            responseCode = "200",
            description = "Lists employees",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmployeeDto.class, type = SchemaType.ARRAY)))
    public Response listEmployees(@BeanParam EmployeePage employeePage) {
        return Response.ok(this.employeeService.listEmployees(employeePage))
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
                    schema = @Schema(implementation = EmployeeDto.class, type = SchemaType.ARRAY)))
    public Response getReport(@BeanParam ReportPage reportPage) {
        return Response
                .ok(this.employeeService.departmentsReport(reportPage.getStartDate(), reportPage.getEndDate()))
                .build();
    }

    @GET
    @Path("/{employeeId}")
    @Operation(summary = "Get employee by employeeId")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get employee",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = EmployeeDto.class, type = SchemaType.OBJECT))),
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

    @GET
    @Path("/select")
    @Operation(summary = "Get all projection of the employee")
    @APIResponse(
            responseCode = "200",
            description = "ID and first name for all employees",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY))
    )
    public Response selectOptions() {
        return Response.ok(this.employeeService.selected()).build();
    }

    @POST
    @Operation(summary = "Creates employee")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Employee created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class, type = SchemaType.STRING))),
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
            schema = @Schema(implementation = EmployeeDto.class))) @Valid EmployeeDto employeeDto, @Context UriInfo uriInfo) {
        employeeDto.setRegisteredBy(keycloakSecurityContext.getPrincipal().getName());
        this.employeeService.saveEmployee(employeeDto);
        URI employeeUri = employeeUriBuilder(employeeDto.employeeId, uriInfo).build();
        return Response.created(employeeUri).build();
    }

    @PUT
    @Path("/{employeeId}")
    @Operation(summary = "Updates employee")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Employee updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = EmployeeDto.class, type = SchemaType.OBJECT))),
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
                    schema = @Schema(implementation = EmployeeDto.class))) @Valid EmployeeDto employeeDto) {
        if (Objects.isNull(employeeDto.getEmployeeId()) || employeeDto.getEmployeeId().isEmpty())
            throw new ServiceException("Employee does not have employeeId");

        if (!Objects.equals(employeeId, employeeDto.getEmployeeId()))
            throw new ServiceException("path variable employeeId does not match Employee.employeeId");

        employeeDto.setUpdatedBy(keycloakSecurityContext.getPrincipal().getName());
        employeeService.updateEmployee(employeeDto);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{employeeId}")
    @Operation(summary = "Deletes existing employee")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Employee deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "404",
                    description = "Could not find employee for a given employeeId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response deleteEmployee(@Parameter(description = "employeeId", required = true)
                                   @PathParam("employeeId") @NotNull String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return Response.noContent().build();
    }
}
