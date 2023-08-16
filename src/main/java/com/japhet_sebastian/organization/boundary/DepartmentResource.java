package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.organization.control.DepartmentService;
import com.japhet_sebastian.organization.entity.DepartmentDetail;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

import static com.japhet_sebastian.organization.boundary.DepartmentResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Department Endpoint", description = "Department operations")
public class DepartmentResource {

    public static final String RESOURCE_PATH = "/departments";

    @Inject
    DepartmentService departmentService;

    @Inject
    Logger LOGGER;

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
        return Response.ok(departmentDetails)
                .header("X-Total-Count", totalCount)
                .build();

//        return null;

//        return Department.findByName(deptName)
//                .map(department -> Response.ok(department).build())
//                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }
//
//    @GET
//    @Path("/{id}")
//    @Operation(summary = "Returns the department for a given identifier")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "200",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
//                    description = "Returns a found department"),
//            @APIResponse(responseCode = "400", description = "Invalid input"),
//            @APIResponse(responseCode = "404", description = "Department is not found for a given identifier")
//    })
//    public Response findDepartment(@Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId) {
//        return departmentService.findDepartment(deptId)
//                .map(department -> Response.ok(department).build())
//                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
//    }
//
//    @GET
//    @Path("/select")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Fetch only department ID and name for all departments available to be used for client side selection options")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "200",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
//                    description = "Department ID and name as key value pair objects for the departments available"),
//            @APIResponse(responseCode = "204", description = "No department available in the database")
//    })
//    public Response departmentSelectOptions() {
//        List<SelectOptions> dept = Department.find("SELECT d.id, d.departmentName FROM Department d")
//                .project(SelectOptions.class).list();
//        if (dept.size() == 0) return Response.status(Status.NO_CONTENT).build();
//        return Response.ok(dept).build();
//    }
//
//    @POST
//    @Operation(summary = "Creates a valid department and stores it into the database")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "201",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
//                    description = "URI of the created department"),
//            @APIResponse(responseCode = "400", description = "Invalid input"),
//            @APIResponse(
//                    responseCode = "409",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
//                    description = "Department duplications is not allowed")
//    })
//    public Response createDepartment(
//            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)))
//            @Valid Department department, @Context UriInfo uriInfo) {
//        boolean isDept = Department.findByName(department.departmentName).isPresent();
//        if (isDept) return Response.status(Status.CONFLICT).entity("Department already exists").build();
//        departmentService.insertDepartment(department);
//        URI deptUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(department.id)).build();
//        return Response.created(deptUri).build();
//    }
//
//    @PUT
//    @Path("/{id}")
//    @Operation(summary = "Updates an existing department")
//    @APIResponses({
//            @APIResponse(responseCode = "204", description = "Department has been successfully updated"),
//            @APIResponse(responseCode = "404", description = "Department to be updated does not exist in the database"),
//            @APIResponse(responseCode = "415", description = "Format is not JSON"),
//            @APIResponse(
//                    responseCode = "409",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
//                    description = "Department payload is not the same as an entity object that needed to be updated")
//    })
//    public Response updateDepartment(
//            @Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId,
//            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)))
//            @Valid Department department) {
//        if (!deptId.equals(department.id))
//            return Response.status(Response.Status.CONFLICT).entity(department).build();
//
//        try {
//            departmentService.updateDepartment(department, deptId);
//        } catch (NotFoundException | NoResultException enf) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        return Response.status(Status.NO_CONTENT).build();
//    }
//
//    @DELETE
//    @Path("/{id}")
//    @Transactional(Transactional.TxType.REQUIRED)
//    @Operation(summary = "Deletes an existing department")
//    @APIResponses({
//            @APIResponse(responseCode = "204", description = "Department has been successfully deleted"),
//            @APIResponse(responseCode = "400", description = "Invalid input"),
//            @APIResponse(responseCode = "404", description = "Department to be deleted does not exist in the database"),
//            @APIResponse(responseCode = "500", description = "Department not found")
//    })
//    public Response deleteDepartment(
//            @Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId) {
//        return Department.deleteById(deptId)
//                ? Response.status(Status.NO_CONTENT).build()
//                : Response.status(Status.NOT_FOUND).build();
//    }
//
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
