package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.service.SupplierService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
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

@Path("/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Supplier Endpoint", description = "This API allows to keep record of asset suppliers")
public class SupplierResource {

    @Context
    UriInfo uriInfo;

    @Inject
    SupplierService supplierService;

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Operation(summary = "Retrieves all available suppliers from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class, type = SchemaType.ARRAY)),
                    description = "Lists all the suppliers"),
            @APIResponse(responseCode = "204", description = "No supplier to display")
    })
    public Response listSuppliers(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer index,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("10") Integer size,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("name") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
        PanacheQuery<Supplier> query = supplierService.listSuppliers(search, column, direction);
        Page currentPage = Page.of(index, size);
        query.page(currentPage);

        Long totalCount = query.count();
        List<Supplier> suppliersForCurrentPage = query.list();
        int lastPage = query.pageCount();
        if (suppliersForCurrentPage.size() == 0) return Response.status(Status.NO_CONTENT).build();


        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, size, lastPage);
        return Response.ok(suppliersForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Returns supplier for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)),
                    description = "Returns a found supplier"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Supplier is not found for a given identifier")
    })
    public Response findSupplier(@Parameter(description = "Supplier identifier", required = true) @PathParam("id") @NotNull Long id) {
        return supplierService.findSupplier(id)
                .map(supplier -> Response.ok(supplier).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @POST
    @Operation(summary = "Creates a valid supplier and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created supplier"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
                    description = "Supplier duplications is not allowed")
    })
    public Response createSupplier(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)))
            @Valid Supplier supplier, @Context UriInfo uriInfo) {
        boolean isSupplier = Supplier.findByEmailAndPhone(supplier.email, supplier.phone).isPresent();
        if (supplier.address == null) return Response.status(Status.BAD_REQUEST).build();
        if (isSupplier)
            return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();

        supplierService.createSupplier(supplier);
        URI supplierURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(supplier.id)).build();
        return Response.created(supplierURI).build();
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieve only suppliers ID and names for all available suppliers in the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "Supplier ID and name as key value pair objects for suppliers available"),
            @APIResponse(responseCode = "204", description = "No supplier available in the database")
    })
    public Response supplierSelectOptions() {
        List<SelectOptions> suppliers = Supplier.find("SELECT s.id, s.name FROM Supplier s")
                .project(SelectOptions.class).list();
        if (suppliers.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(suppliers).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing supplier")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Supplier has been successfully updated"),
            @APIResponse(responseCode = "404", description = "Supplier to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)),
                    description = "Supplier payload is not the same as an entity object that needed to be updated")
    })
    public Response updateSupplier(
            @Parameter(description = "Supplier identifier", required = true) @PathParam("id") @NotNull Long supplierId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)))
            @Valid Supplier supplier) {
        if (!supplierId.equals(supplier.id)) return Response.status(Response.Status.CONFLICT).entity(supplier).build();

        try {
            supplierService.updateSupplier(supplier, supplierId);
        } catch (NotFoundException | NoResultException enf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing supplier")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Supplier has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Supplier to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Supplier not found")
    })
    public Response delete(@Parameter(description = "Supplier identifier", required = true) @PathParam("id") @NotNull Long suppId) {
        try {
            supplierService.deleteSupplier(suppId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

}
