package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.College;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.service.CollegeService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
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

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/college")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "College Endpoint", description = "This API allows to create college and associated departments")
public class CollegeResource {

    @Inject
    CollegeService collegeService;

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all available college from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = College.class, type = SchemaType.ARRAY)),
                    description = "Lists all the colleges"),
            @APIResponse(responseCode = "204", description = "No college to display")
    })
    public Response allColleges(
            @Context UriInfo uriInfo,
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer page,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("5") Integer size,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search) {

        PanacheQuery<College> query = collegeService.listColleges(search);
        Page currentPage = Page.of(page, size);
        query.page(currentPage);

        Long totalCount = query.count();
        List<College> departmentsForPage = query.list();
        int lastPage = query.pageCount();
        if (departmentsForPage.isEmpty())
            return Response.status(Response.Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, size, lastPage);

        return Response.ok(departmentsForPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{college-id}")
    @Operation(summary = "Returns the college for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = College.class)),
                    description = "Returns a found college"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "College is not found for a given identifier")
    })
    public Response getCollege(
            @Parameter(description = "Department identifier", required = true) @PathParam("college-id") @NotNull UUID collegeId) {
        return collegeService.findCollege(collegeId)
                .map(college -> Response.ok(college).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Fetch only college ID and name for all colleges available to be used for client side selection options")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "College ID and name as key value pair objects for the colleges available"),
            @APIResponse(responseCode = "204", description = "No college available in the database")
    })
    public Response collegeSelectOptions() {
//        List<SelectOptions> colleges = College.find("SELECT c.id, c.collegeName FROM College c")
//                .project(SelectOptions.class).list();

        List<SelectOptions> colleges = collegeService.selected();
        if (colleges.isEmpty()) return Response.status(Response.Status.NO_CONTENT).build();
        return Response.ok(colleges).build();
    }

    @POST
    @Operation(summary = "Creates a valid college and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created college"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = College.class)),
                    description = "College duplications is not allowed")
    })
    public Response createCollege(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = College.class)))
            @Valid College college, @Context UriInfo uriInfo) {
        boolean isCollege = collegeService.collegeExists(college.getCollegeName());
        if (isCollege) return Response.status(Response.Status.CONFLICT).entity("College already exists").build();
        collegeService.addCollege(college);
        URI collegeURI = uriInfo.getAbsolutePathBuilder().path(college.getCollegeId().toString()).build();
        return Response.created(collegeURI).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing college")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "College has been successfully updated"),
            @APIResponse(responseCode = "404", description = "College to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = College.class)),
                    description = "College payload is not the same as an entity object that needed to be updated")
    })
    public Response updateCollege(
            @Parameter(description = "College identifier", required = true) @PathParam("id") @NotNull UUID collegeId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = College.class))) @Valid College college) {
        if (!collegeId.equals(college.getCollegeId()))
            return Response.status(Response.Status.CONFLICT).entity(college).build();

        try {
            collegeService.updateCollege(college, collegeId);
        } catch (NotFoundException | NoResultException enf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional(Transactional.TxType.REQUIRED)
    @Operation(summary = "Deletes an existing college")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "College has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "College to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "College not found")
    })
    public Response deleteCollege(
            @Parameter(description = "College identifier", required = true) @PathParam("id") @NotNull UUID collegeId) {
        return collegeService.deleteCollege(collegeId)
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
