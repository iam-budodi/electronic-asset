package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.control.DepartmentService;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import com.japhet_sebastian.organization.entity.DepartmentInput;
import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
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
import java.util.List;
import java.util.Objects;

import static com.japhet_sebastian.organization.boundary.DepartmentResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Department Endpoint", description = "Department operations")
public class DepartmentResource extends AbstractDepartmentType {

    public static final String RESOURCE_PATH = "/departments";

    @Inject
    DepartmentService departmentService;

    @GET
    @Operation(summary = "Get all available departments information")
    @APIResponse(
            responseCode = "200",
            description = "Lists all the departments information",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DepartmentDetail.class, type = SchemaType.ARRAY)))
    public Response allDepartments(@BeanParam PageRequest pageRequest) {
        List<DepartmentDetail> departmentDetails = this.departmentService.listDepartments(pageRequest);
        Long totalCount = this.departmentService.totalDepartments();
        return Response.ok(departmentDetails).header("X-Total-Count", totalCount).build();
    }

    @GET
    @Path("/{departmentId}")
    @Operation(summary = "Get department details for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get department by department identifier",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = DepartmentDetail.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "Department does not exist for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response getDepartment(
            @Parameter(description = "Department identifier", required = true)
            @PathParam("departmentId") @NotNull String departmentId) {
        return this.departmentService.getDepartment(departmentId)
                .map(department -> Response.ok(department).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Operation(summary = "Get all selection options projection of the departments")
    @APIResponse(
            responseCode = "200",
            description = "Return an object with identifier and name as key value pair",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY))
    )
    public Response selectOptions() {
        return Response.ok(this.departmentService.selected()).build();
    }

    @POST
    @Operation(summary = "Creates valid department")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "URI of the created department",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class, type = SchemaType.STRING))),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Department with same name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response saveDepartment(@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = DepartmentInput.class))) @Valid DepartmentInput departmentInput, @Context UriInfo uriInfo) {
        this.departmentService.addDepartment(departmentInput);
        URI departmentUri = departmentUriBuilder(departmentInput.getDepartmentId(), uriInfo).build();
        return Response.created(departmentUri).build();
    }

    @PUT
    @Path("/{departmentId}")
    @Operation(summary = "Updates existing department")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Department updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = DepartmentInput.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "No Department found for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid Department input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Department does not have identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Path variable departmentId does not match Department.departmentId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response updateDepartment(
            @Parameter(description = "departmentId", required = true) @PathParam("departmentId") @NotNull String departmentId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DepartmentInput.class))) @Valid DepartmentInput department) {
        if (Objects.isNull(department.getDepartmentId()) || department.getDepartmentId().isEmpty())
            throw new ServiceException("Department does not have departmentId");

        if (!Objects.equals(departmentId, department.getDepartmentId()))
            throw new ServiceException("path variable departmentId does not match Department.departmentId");

        departmentService.updateDepartment(department);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{departmentId}")
    @Operation(summary = "Deletes existing department")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "College deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "40",
                    description = "No department found for departmentId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response deleteDepartment(
            @Parameter(description = "departmentId", required = true) @PathParam("departmentId") @NotNull String departmentId) {
        this.departmentService.deleteDepartment(departmentId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

//    @GET
//    @Path("/{id}/employees")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Retrieves all employees available in a specific deparment given an identifier")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "200",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
//                    description = "Lists all employees from the department identified by the department identifier"),
//            @APIResponse(responseCode = "204", description = "No employee in a specified department"),
//            @APIResponse(responseCode = "404", description = "Employee does not exist for a given work identifier query")
//    })
//    public Response listAllEmployeeFromDepartment(
//            @Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long departmentId,
//            @Parameter(description = "Employee work identifier", required = false) @QueryParam("workid") String workId) {
////		String queryString = "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address ";
////		List<Employee> employees = Employee.find(queryString + "WHERE d.id = ?1",
////				Sort.by("e.firstName").and("e.lastName"),
////				departmentId).list();
//
////		if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
////		if (workId == null) return Response.ok(employees).build();
//
////		return Employee.find(queryString + "WHERE e.workId LIKE :workId",
////				Parameters.with("workId", "%" + workId + "%"))
////				.firstResultOptional()
////				.map(employee -> Response.ok(employee).build())
////				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
//
////		TODO: TRIAL.... delete the commented when this is fully tested
//        PanacheQuery<Employee> employeeQuery = Employee.find(
//                "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address "
//                        + "WHERE d.id = :departmentId AND (:workId IS NULL OR e.workId = :workId)",
//                Sort.by("e.firstName").and("e.lastName"),
//                Parameters.with("departmentId", departmentId).and("workId", "%" + workId + "%"));
//
//        List<Employee> employees = employeeQuery.list();
//        if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
//        if (workId == null) return Response.ok(employees).build();
//
//        return employeeQuery.firstResultOptional()
//                .map(employee -> Response.ok(employee).build())
//                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
//    }
}
