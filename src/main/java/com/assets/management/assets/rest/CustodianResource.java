package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.EmployeeAsset;
import com.assets.management.assets.service.CustodianService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("allocations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
public class CustodianResource {

    /**
     * TODO: remove all the @PathParam and fix code related to any IDs
     * TODO: test to confirm business logic is intact
     */

    @Context
    UriInfo uriInfo;

    @Inject
    LinkHeaderPagination headerPagination;
    @Inject
    CustodianService custodianService;

    @Inject
    Logger LOG;

    @POST
    @Path("/assign")
    @Operation(summary = "Allocates an asset to employee")
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
        if (allocation.asset.id == null || allocation.employee.id == null) return Response.status(Response.Status.BAD_REQUEST).build();

        URI allocationURI;
        try {
            allocation = custodianService.allocate(allocation);
            allocationURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)).build();

            LOG.info("ALLOCATED ASSET ID: " + allocation.asset.id);
            custodianService.allocationQRString(allocation.asset, allocationURI);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).entity("Employee/Asset don't exist").build();
        } catch (ClientErrorException ce) {
            return Response.status(Response.Status.CONFLICT).entity("Asset is already taken!").build();
        }

        return Response.created(allocationURI).build();
    }

    @POST
    @Path("/transfer")
    @Operation(summary = "Transfers an asset to another custodian")
    @APIResponses({
            @APIResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = URI.class)), description = "URI of the transfer record"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = String.class)), description = "Duplicates is not allowed"),
            @APIResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = String.class)), description = "Custodian or asset does not exist")
    })
    public Response transfer(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Transfer.class))) @Valid Transfer transfer,
            @Context UriInfo uriInfo) {
        if (transfer.prevCustodian.id == null || transfer.asset.id == null || transfer.currentCustodian.id == null) return Response.status(Response.Status.BAD_REQUEST).build();

        URI transferURI = null;
//        String uriSubPath = "employees" + "/" + Long.toString(transfer.currentCustodian.id) + "/" + "allocates" + "/";

        try {
            Transfer transferred = custodianService.transfer(transfer);
            transferURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(transfer.id)).build();
//            transferURI = uriInfo.getBaseUriBuilder().path(uriSubPath + Long.toString(transfer.id)).build();
//            transferURI = uriInfo.getBaseUriBuilder().path(Long.toString(transfer.id)).build();
            LOG.info("TRANSFERRED URI : " + transferURI.toString());
            custodianService.transferQRString(transferred, transferURI);
        } catch (NoResultException ex) {
            transfer = null;
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).entity("Custodian don't exist").build();
        } catch (BadRequestException br) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ensure Asset is transferred from the current custodian").build();
        } catch (ClientErrorException ce) {
            return Response.status(Response.Status.CONFLICT).entity("Asset cannot be transferred!").build();
        }

        if (transfer == null) return Response.status(Response.Status.NOT_FOUND).entity("Asset was not found").build();
        return Response.created(transferURI).build();
    }

    @GET
    @Path("/assign")
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
        PanacheQuery<Allocation> query = custodianService.listAllocations(search, allocationDate, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        long totalCount = query.count();
        List<Allocation> allocationsForCurrentPage = query.list();
        int lastPage = query.pageCount();

        if (allocationsForCurrentPage.size() == 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(allocationsForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/transfer")
    @Operation(summary = "Retrieves details of all transfers")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Transfer.class, type = SchemaType.ARRAY)),
                    description = "Lists all the transfers"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response listTransfers(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Search date", required = false) @QueryParam("date") LocalDate transferDate,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("transferDate") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
//        LOG.info("GOT TO THE SERVICE");
//        Instant date = null;
//        if (allocationDate != null)
//            date = allocationDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        PanacheQuery<Allocation> query = custodianService.listAllTransfers(search, transferDate, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        long totalCount = query.count();
        List<Allocation> transfersForCurrentPage = query.list();
        int lastPage = query.pageCount();

        if (transfersForCurrentPage.size() == 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(transfersForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{employeeId}")
    @Operation(summary = "Retrieves details of all allocations per employee")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(oneOf = {Allocation.class, Transfer.class}, type = SchemaType.ARRAY)),
                    description = "Lists all the employee's allocations either assigned or transferred"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")})
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response employeeAllAssets(
            @Parameter(description = "Employee Identifier", required = true) @PathParam("employeeId") @NotNull Long employeeId,
            @Parameter(description = "Status search", required = false) @QueryParam("status") AllocationStatus filteredStatus
    ) {
        List<EmployeeAsset> assets = custodianService.getEmployeeAssets(filteredStatus, employeeId);
        if (assets.size() == 0) return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(assets).build();
    }


    @GET
    @Path("/assign/{allocationId}")
    @Operation(summary = "Returns the scanned QR Code details for allocated assets")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(oneOf = {Allocation.class, Transfer.class})), description = "Encoded QR Code details"),
            @APIResponse(responseCode = "204", description = "No record found")
    })
    public Response allocationDetailsScanned(
            @Parameter(description = "Allocation identifier", required = true) @PathParam("allocationId") @NotNull Long allocationId
    ) {
        Allocation allocated = Allocation.qrDetails(allocationId);

        if (allocated == null)
            return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(allocated).build();
    }


    @GET
    @Path("/transfer/{transferId}")
    @Operation(summary = "Returns the scanned QR Code details for transferred assets")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(oneOf = {Allocation.class, Transfer.class})), description = "Encoded QR Code details"),
            @APIResponse(responseCode = "204", description = "No record found")
    })
    public Response transferDetailsScanned(
            @Parameter(description = "Transfer identifier", required = true) @PathParam("transferId") @NotNull Long transferId
    ) {
        Transfer transferred = Transfer.qrDetails(transferId);

        if (transferred == null)
            return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(transferred).build();
    }
}
