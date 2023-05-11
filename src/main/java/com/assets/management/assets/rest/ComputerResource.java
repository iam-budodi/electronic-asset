package com.assets.management.assets.rest;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.assets.management.assets.model.entity.*;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
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

import io.quarkus.hibernate.orm.panache.Panache;

@Path("/computers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@Tag(name = "Computer Endpoint", description = "This API allows to keep inventory of all purchased computers")
public class ComputerResource {

    @Inject
    Logger LOG;

    @Context
    UriInfo uriInfo;

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all available computers from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class, type = SchemaType.ARRAY)),
                    description = "Lists all the computers"),
            @APIResponse(responseCode = "204", description = "No computer to display"),
    })
    public Response listAllComputers(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Search date", required = false) @QueryParam("date") LocalDate date,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("brand") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
        PanacheQuery<Asset> query = Computer.getAll(search, date, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        Long totalCount = query.count();
        List<Computer> computersForCurrentPage = query.list();
        int lastPage = query.pageCount();
        if (computersForCurrentPage.size() == 0) return Response.status(Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(computersForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Returns the computer for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Returns a found computer"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "computer is not found for a given identifier")
    })
    public Response findComputer(
            @Parameter(description = "Computer identifier", required = true) @PathParam("id") @NotNull Long computerId) {
        return Computer.getById(computerId).firstResultOptional()
                .map(computer -> Response.ok(computer).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Fetch only computer ID and brand for all computers available to be used for client side selection options")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "Computer ID and model as key value pair objects for the computers available"),
            @APIResponse(responseCode = "204", description = "No computer available in the database")
    })
    public Response computerSelectOptions() {
        List<SelectOptions> allocates = Asset.find(
                        "SELECT c.id, c.model FROM Computer c " +
                                "WHERE c.id NOT IN " +
                                "(SELECT a.asset.id FROM Allocation a WHERE :allocated MEMBER OF a.status OR :retired MEMBER OF a.status) " +
                                "AND c.id NOT IN " +
                                "(SELECT t.asset.id FROM Transfer t WHERE :transferStatuses MEMBER OF t.status)",
                        Parameters.with("allocated", Set.of(AllocationStatus.ALLOCATED)).and("retired", Set.of(AllocationStatus.RETIRED))
                                .and("transferStatuses", List.of(AllocationStatus.ALLOCATED))
                ).project(SelectOptions.class).list();

        if (allocates.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(allocates).build();
    }

    @POST
    @Operation(summary = "Creates a valid computer and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created computer"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Computer duplications is not allowed"),
            @APIResponse(responseCode = "404", description = "Purchase order for the computer item does not exist in the database")
    })
    public Response createComputer(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)))
            @Valid Computer computer, @Context UriInfo uriInfo) {
        LOG.info("CHECKING FOR PURCHASE OBJ: " + computer.purchase.id);
        if (Computer.checkSerialNumber(computer.serialNumber))
            return Response.status(Status.CONFLICT).entity("Duplicate is not allow!").build();
        if (computer.purchase == null || computer.purchase.id == null)
            return Response.status(Status.BAD_REQUEST).entity("Invalid purchase details").build();

        return Purchase.findByIdOptional(computer.purchase.id).map(purchase -> {
            Computer.persist(computer);
            URI computerUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(computer.id)).build();
            return Response.created(computerUri).build();
        }).orElseGet(() -> Response.status(Status.NOT_FOUND).entity("Purchase record dont exists").build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing computer")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Computer has been successfully updated"),
            @APIResponse(responseCode = "404", description = "Computer to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Computer payload is not the same as an entity object that needed to be updated")
    })
    public Response updateComputer(
            @Parameter(description = "Computer identifier", required = true) @PathParam("id") @NotNull Long computerId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)))
            @Valid Computer computer) {
        if (!computerId.equals(computer.id))
            return Response.status(Response.Status.CONFLICT).entity(computer).build();

        return Computer.findByIdOptional(computerId).map(exists -> {
            Panache.getEntityManager().merge(computer);
            return Response.status(Status.NO_CONTENT).build();
        }).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing computer")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Computer has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Computer to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Computer not found")
    })
    public Response deleteComputer(
            @Parameter(description = "Computer identifier", required = true) @PathParam("id") @NotNull Long computerId) {
        return Computer.deleteById(computerId)
                ? Response.status(Status.NO_CONTENT).build()
                : Response.status(Status.NOT_FOUND).build();
    }
}
