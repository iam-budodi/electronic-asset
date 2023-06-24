package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.oidc.IdToken;
import io.quarkus.panache.common.Page;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Path("/employees")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Employee Endpoint", description = "This API allows to keep track of all the employees assigned the assets")
public class EmployeeResource {

    @Context
    UriInfo uriInfo;

    @Inject
    EmployeeService employeeService;

    @Inject
    LinkHeaderPagination headerPagination;

    @Inject
    Logger LOG;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @GET
//    @Authenticated
//    @RolesAllowed("procure")
    @Operation(summary = "Retrieves all available employees from the database")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
                    description = "Lists all the employees"),
            @APIResponse(responseCode = "204", description = "No employee to display"),
    })
    public Response listEmployees(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Search date", required = false) @QueryParam("date") LocalDate date,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("firstName") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
        LOG.info("ID TOKEN : " + idToken.getName());
        PanacheQuery<Employee> query = employeeService.listEmployees(search, date, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        Long totalCount = query.count();
        List<Employee> employeesForCurrentPage = query.list();
        //      int lastPage = (int) ((totalCount + size - 1)); // uncomment to test
        int lastPage = query.pageCount();
        if (employeesForCurrentPage.size() == 0) return Response.status(Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(employeesForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Returns the employee for a given identifier")
    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Returns a found employee"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee is not found for a given identifier")})
    public Response findEmployee(@Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long empId) {
        return employeeService.findById(empId).map(employee -> Response.ok(employee).build()).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieve only employee ID and first name from all employees in the database")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "ID and first name for all employees available"),
            @APIResponse(responseCode = "204", description = "No employee available in the database")
    })
    public Response employeesSelectOptions() {
        List<SelectOptions> employees = Employee.find("SELECT e.id, e.firstName || ' ' || CONCAT(SUBSTRING(e.middleName, 1, 1), '.') || ' ' || e.lastName FROM Employee e")
                .project(SelectOptions.class).list();
        if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();

        return Response.ok(employees).build();
    }

    @POST
    @Operation(summary = "Creates a valid employee and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created employee"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
                    description = "Employee duplications is not allowed"),
            @APIResponse(
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
                    description = "Specified department does not exist in the database")})
    public Response createEmployee(@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class))) @Valid Employee employee, @Context UriInfo uriInfo) {
        if (employee.address == null || employee.department == null) return Response.status(Status.BAD_REQUEST).build();
        else if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
            if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
                return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
            else if (!Department.findByIdOptional(employee.department.id).isPresent())
                return Response.status(Status.NOT_FOUND).entity("Department dont exists").build();

        employee = employeeService.addEmployee(employee);
        URI employeeUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(employee.id)).build();
        return Response.created(employeeUri).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing employee")
    @APIResponses({@APIResponse(responseCode = "204", description = "Employee has been successfully updated"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee to be updated does not exist in the database"), @APIResponse(responseCode = "415", description = "Format is not JSON"), @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Employee payload is not the same as an entity object that needed to be updated")})
    public Response updateEmployee(@Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId, @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class))) @Valid Employee employee) {
        if (!empId.equals(employee.id)) return Response.status(Response.Status.CONFLICT).entity(employee).build();
        else if (employee.department == null) return Response.status(Status.BAD_REQUEST).build();

        employee.address = null;
        try {
            employeeService.updateEmployee(employee, empId);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing employee")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Employee has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Employee to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Employee not found")})
    public Response deleteEmployee(
            @Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId
    ) {
        try {
            employeeService.deleteEmployee(empId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
