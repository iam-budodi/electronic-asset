package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.service.TransferService;
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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
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
import java.time.LocalDate;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("transfers")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@Tag(name = "Transfer Endpoint", description = "Allows to keep track of all change of ownership")
public class TransferResource {

    @Context
    UriInfo uriInfo;

    @Inject
    LinkHeaderPagination headerPagination;

    @Inject
    TransferService transferService;

    @Inject
    Logger LOG;

    @POST
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
            @Context UriInfo uriInfo
    ) {
        if (transfer.prevCustodian.id == null || transfer.asset.id == null || transfer.currentCustodian.id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        URI transferURI = null;
        try {
            Transfer transferred = transferService.transfer(transfer);
            transferURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(transfer.id)).build();
            LOG.info("TRANSFERRED URI : " + transferURI.toString());
            transferService.transferQRString(transferred, transferURI);
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
        PanacheQuery<Allocation> query = transferService.listAllTransfers(search, transferDate, column, direction);
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
    @Path("/{transferId}")
    @Operation(summary = "Returns the scanned QR Code details for transferred assets")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Transfer.class, type = SchemaType.ARRAY)), description = "Encoded QR Code details"),
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

    @PUT
    @Path("/{transferId}")
    @Operation(summary = "Updates a given transfer record")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Transfer record has been successfully updated"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Transfer record to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Transfer.class)),
                    description = "Transfer payload is not the same as an entity object that needed to be updated")
    })
    public Response updateTransfer(
            @Parameter(description = "Transfer identifier", required = true) @PathParam("transferId") @NotNull Long transferId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Transfer.class))) @Valid Transfer transfer
    ) {
        if (!transferId.equals(transfer.id))
            return Response.status(Response.Status.CONFLICT).entity(transfer).build();
//        else if (allocation.department == null) return Response.status(Response.Status.BAD_REQUEST).build();

//        employee.address = null;
        try {
            transferService.updateTransfer(transfer, transferId);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{transferId}")
    @Operation(summary = "Deletes a given transfer record")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Transfer record has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Transfer record to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Transfer record not found")
    })
    public Response deleteTransfer(
            @Parameter(description = "Transfer identifier", required = true) @PathParam("transferId") @NotNull Long transferId
    ) {
        try {
            transferService.deleteTransfer(transferId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
