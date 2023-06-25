package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.EmployeeAsset;
import com.assets.management.assets.service.AssignmentService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("allocations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Transactional(Transactional.TxType.REQUIRED)
@Tag(name = "Allocation Endpoint", description = "Allows to keep track of all the employees assigned assets for the first time")
public class AssignmentResource {

    @Context
    UriInfo uriInfo;

    @ConfigProperty(name = "client.url", defaultValue = "Check the URL in config file")
    String clientURL;

    @Inject
    LinkHeaderPagination headerPagination;
    @Inject
    AssignmentService assignmentService;

    @Inject
    Logger LOG;

    @POST
    @Operation(summary = "Allocates an asset to employee for the first time")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class)), description = "URI of the allocation record"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = String.class)), description = "Duplicates is not allowed"),
            @APIResponse(
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = String.class)), description = "Employee to be assigned an asset or the asset does not exist")
    })
    public Response allocate(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Allocation.class))) @Valid Allocation allocation,
            @Context UriInfo uriInfo
    ) {
        if (allocation.asset.id == null || allocation.employee.id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        URI allocationURI;
        try {
            allocation = assignmentService.allocate(allocation);
            allocationURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)).build();

            URI qrRedirectURL = URI.create(clientURL + uriInfo.getPath() + "/" + allocation.id);

            LOG.info("ALLOCATED ASSET ID: " + allocation.asset.id);
            LOG.info("FRONT-END URL : " + qrRedirectURL);
            assignmentService.allocationQRString(allocation.asset, qrRedirectURL);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).entity("Employee/Asset don't exist").build();
        } catch (ClientErrorException ce) {
            return Response.status(Response.Status.CONFLICT).entity("Asset is already taken!").build();
        }

        return Response.created(allocationURI).build();
    }

    @GET
    @Operation(summary = "Retrieves details of all allocations")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Allocation.class, type = SchemaType.ARRAY)),
                    description = "Lists all the allocations"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response listAllocations(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Search date", required = false) @QueryParam("date") LocalDate allocationDate,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("allocationDate") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
        PanacheQuery<Allocation> query = assignmentService.listAllocations(search, allocationDate, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        long totalCount = query.count();
        int lastPage = query.pageCount();
        List<Allocation> allocationsForCurrentPage = query.list();

        if (allocationsForCurrentPage.size() == 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(allocationsForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    //    TODO: Split this twice for transfer and assignment
    @GET
//    @Path("/{employeeId}") OG
    @Path("/assets")
    @Operation(summary = "Retrieves details of all allocations per employee")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmployeeAsset.class, type = SchemaType.ARRAY)),
                    description = "Lists all the employee's allocations either assigned or transferred"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")})
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response employeeAllAssets(
//            @Parameter(description = "Employee Identifier", required = true) @PathParam("employeeId") @NotNull Long employeeId, OG
            @Parameter(description = "Search by work ID", required = true) @QueryParam("work-id") @NotNull String workId,
            @Parameter(description = "Status search", required = false) @QueryParam("status") AllocationStatus filteredStatus
    ) {
        List<EmployeeAsset> assets = assignmentService.getEmployeeAssets(filteredStatus, workId);
        if (assets.size() == 0) return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(assets).build();
    }


    @GET
    @Path("{allocationId}")
    @Operation(summary = "Returns the scanned QR Code details for allocated assets")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Allocation.class)), description = "Encoded QR Code details"),
            @APIResponse(responseCode = "204", description = "No record found")
    })
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response allocationDetailsScanned(
            @Parameter(description = "Allocation identifier", required = true) @PathParam("allocationId") @NotNull Long allocationId
    ) {
        Allocation allocated = Allocation.qrDetails(allocationId);

        if (allocated == null)
            return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(allocated).build();
    }

    @PUT
    @Path("/{allocationId}")
    @Operation(summary = "Updates an existing allocation")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Allocation has been successfully updated"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Allocation to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Allocation.class)),
                    description = "Allocation payload is not the same as an entity object that needed to be updated")
    })
    public Response updateAllocation(
            @Parameter(description = "Allocation identifier", required = true) @PathParam("allocationId") @NotNull Long allocationId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Allocation.class))) @Valid Allocation allocation
    ) {
        if (!allocationId.equals(allocation.id))
            return Response.status(Response.Status.CONFLICT).entity(allocation).build();
//        else if (allocation.department == null) return Response.status(Response.Status.BAD_REQUEST).build();

//        employee.address = null;
        try {
            assignmentService.updateAllocation(allocation, allocationId);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{allocationId}")
    @Operation(summary = "Deletes a given allocation record")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Allocation has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Allocation to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Allocation record not found")
    })
    public Response deleteAllocation(
            @Parameter(description = "Allocation identifier", required = true) @PathParam("allocationId") @NotNull Long allocationId
    ) {
        try {
            assignmentService.deleteAllocation(allocationId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
