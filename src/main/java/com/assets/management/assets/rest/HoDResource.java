package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.HeadOfDepartment;
import com.assets.management.assets.service.HoDService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
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
import java.util.List;


@Path("/hods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "HoD Endpoint", description = "This API allows to register head of departments ")
public class HoDResource {

    @Inject
    Logger LOG;

    @Inject
    HoDService hoDService;

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all available head of departments from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = HeadOfDepartment.class, type = SchemaType.ARRAY)),
                    description = "Lists all the HoDs"),
            @APIResponse(responseCode = "204", description = "No record to display"),
            @APIResponse(responseCode = "404", description = "HoD is not found")
    })
    public Response allHoDs(
            @Context UriInfo uriInfo,
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer page,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("5") Integer size,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search) {

        PanacheQuery<HeadOfDepartment> query = hoDService.listHoDs(search);
        Page currentPage = Page.of(page, size);
        query.page(currentPage);

        Long totalCount = query.count();
        List<HeadOfDepartment> hodsCurrentPage = query.list();
        int lastPage = query.pageCount();
        if (hodsCurrentPage.size() == 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, size, lastPage);

        return Response.ok(hodsCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Returns the head of department for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = HeadOfDepartment.class)),
                    description = "Returns a found head of department"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Head of department is not found for a given identifier")
    })
    public Response getHoD(
            @Parameter(description = "Head of department identifier", required = true) @PathParam("id") @NotNull Long hodId) {
        return hoDService.getHoD(hodId)
                .map(hod -> Response.ok(hod).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }
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
//        if (dept.size() == 0) return Response.status(Response.Status.NO_CONTENT).build();
//        return Response.ok(dept).build();
//    }

    @POST
    @Operation(summary = "Adds a valid head of department object and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the added head of department"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = HeadOfDepartment.class)),
                    description = "duplications is not allowed")
    })
    public Response addHod(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = HeadOfDepartment.class)))
            @Valid HeadOfDepartment hod, @Context UriInfo uriInfo) {
        boolean isHoD = HeadOfDepartment.find("#HoD.workId",
                Parameters.with("workId", hod.employee.workId.toLowerCase())).firstResultOptional().isPresent();

        if (isHoD) return Response.status(Response.Status.CONFLICT).entity("Head of department already exists").build();
        hoDService.addHoD(hod);
        URI hodUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(hod.id)).build();
        return Response.created(hodUri).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing head of department")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Object has been successfully updated"),
            @APIResponse(responseCode = "404", description = "Object to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = HeadOfDepartment.class)),
                    description = "Head of department payload is not the same as an entity object that needed to be updated")})
    public Response updateHoD(
            @Parameter(description = "HoD identifier", required = true) @PathParam("id") @NotNull Long hodId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = HeadOfDepartment.class))) @Valid HeadOfDepartment hod) {
        if (!hodId.equals(hod.id))
            return Response.status(Response.Status.CONFLICT).entity(hod).build();

        try {
            hoDService.updateHoD(hod, hodId);
        } catch (NotFoundException | NoResultException enf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional(Transactional.TxType.REQUIRED)
    @Operation(summary = "Deletes an existing head of department object")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Head of department has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Head of department to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Head of department not found")
    })
    public Response deleteHoD(
            @Parameter(description = "HoD identifier", required = true) @PathParam("id") @NotNull Long hodId) {
        return HeadOfDepartment.deleteById(hodId)
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.NOT_FOUND).build();
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
//        if (employees.size() == 0) return Response.status(Response.Status.NO_CONTENT).build();
//        if (workId == null) return Response.ok(employees).build();
//
//        return employeeQuery.firstResultOptional()
//                .map(employee -> Response.ok(employee).build())
//                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
//    }
}
