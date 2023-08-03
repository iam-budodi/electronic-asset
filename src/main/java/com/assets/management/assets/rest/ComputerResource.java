package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Purchase;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
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

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Path("/computers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Computer Endpoint", description = "This API allows to keep inventory of all purchased computers")
public class ComputerResource {

    @Inject
    Logger LOG;

    @Context
    UriInfo uriInfo;

    @Inject
    SecurityIdentity keycloakSecurityContext;

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
    @Path("report")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves a specified range of computers record for generating report")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Computer.class, type = SchemaType.ARRAY)),
                    description = "Generate computers report"),
            @APIResponse(responseCode = "204", description = "No data for report"),
    })
    public Response unPaginatedList(
            @Parameter(description = "Search date", required = false) @QueryParam("start") LocalDate startDate,
            @Parameter(description = "Search endDate", required = false) @QueryParam("end") LocalDate endDate
    ) {
        String queryString = "SELECT DISTINCT a FROM Asset a LEFT JOIN FETCH a.category LEFT JOIN FETCH a.label "
                + "LEFT JOIN FETCH a.purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
                + "WHERE a.createdAt BETWEEN :startDate AND :endDate";

        List<Asset> assets = Computer.find(queryString, Sort.by("a.brand", Sort.Direction.Descending),
                Parameters.with("startDate", startDate).and("endDate", endDate)).list();
        if (assets.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(assets).build();

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
    @Path("/select-allocation")
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
    public Response allocationSelectOptions() {
        List<SelectOptions> allocates = Asset.find(
                "SELECT c.id, c.brand || ' ' || c.model FROM Computer c " +
                        "WHERE c.id NOT IN " +
                        "(SELECT a.asset.id FROM Allocation a WHERE a.status = :allocated OR a.status = :retired) " +
                        "AND c.id NOT IN " +
                        "(SELECT t.asset.id FROM Transfer t WHERE t.status = :transferStatuses)",
                Parameters.with("allocated", AllocationStatus.ALLOCATED).and("retired", AllocationStatus.RETIRED)
                        .and("transferStatuses", AllocationStatus.ALLOCATED)
        ).project(SelectOptions.class).list();

        if (allocates.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(allocates).build();
    }

    @GET
    @Path("/select-transfer")
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
    public Response transferSelectOptions() {
        List<SelectOptions> allocates = Asset.find(
                "SELECT c.id, c.brand || ' ' || c.model FROM Computer c " +
                        "WHERE c.id IN " +
                        "(SELECT a.asset.id FROM Allocation a WHERE a.status = :allocated) " +
                        "OR c.id IN " +
                        "(SELECT t.asset.id FROM Transfer t WHERE t.status = :transferStatuses)",
                Parameters.with("allocated", AllocationStatus.ALLOCATED)
                        .and("transferStatuses", AllocationStatus.ALLOCATED)
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

        computer.createdBy = keycloakSecurityContext.getPrincipal().getName();
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

        computer.updatedBy = keycloakSecurityContext.getPrincipal().getName();
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
