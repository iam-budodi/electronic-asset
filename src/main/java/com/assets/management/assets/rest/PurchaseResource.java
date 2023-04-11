package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Purchase;
import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.PurchasePerSupplier;

import io.quarkus.hibernate.orm.panache.Panache;

@Path("/purchases")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@Tag(name = "Purchase Endpoint", description = "This API allows to keep track of all the purchases")
public class PurchaseResource {

    @GET
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all available purchases from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class, type = SchemaType.ARRAY)),
                    description = "Lists all the purchases"),
            @APIResponse(responseCode = "204", description = "No purchase to display"),
    })
    public Response listAllPurchases(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize) {
        List<Purchase> purchases = Purchase.retrieveAllOrById().page(pageIndex, pageSize).list();
        if (purchases.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(purchases).build();
    }

    @GET
    @Path("/{id}")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Returns the purchase record for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Returns a found purchase record"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Purchase record is not found for a given identifier")
    })
    public Response findPurchase(
            @Parameter(description = "Purchase identifier", required = true) @PathParam("id") @NotNull Long purchaseId) {
        return Purchase.retrieveAllOrById(purchaseId).firstResultOptional()
                .map(purchase -> Response.ok(purchase).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/count")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Counts all purchases per each supplier in the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PurchasePerSupplier.class, type = SchemaType.ARRAY)),
                    description = "Number of all purchase for each supplier available"),
            @APIResponse(responseCode = "204", description = "No purchase available in the database")
    })
    public Response countPurchasePerSupplier() {
        List<PurchasePerSupplier> purchasesPerSupplier = Purchase.purchasePerSupplier();
        if (purchasesPerSupplier.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(purchasesPerSupplier).build();
    }

    @GET
    @Path("/{invoice}/assets")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all available assets for particular purchase from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Asset.class, type = SchemaType.ARRAY)),
                    description = "Lists all the purchases per supplier"),
            @APIResponse(responseCode = "204", description = "No purchase to display"),
    })
    public Response listAllAssetsPerPurchase(
            @Parameter(description = "Invoice for particular purchase record", required = true) @PathParam("invoice") @NotNull String invoiceNumber,
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pSize) {
        List<Asset> assets = Asset.getAssetByInvoice(invoiceNumber).page(pIndex, pSize).list();
        if (assets.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(assets).build();
    }

    @POST
    @Operation(summary = "Creates a valid purchase record and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created purchase record"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Purchase record duplications is not allowed"),
            @APIResponse(responseCode = "404", description = "Supplier for the purchase does not exist in the database")
    })
    public Response makePurchase(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Purchase.class)))
            @Valid Purchase purchase, @Context UriInfo uriInfo) {
        boolean isDuplicate = Purchase.findByInvoice(purchase.invoiceNumber).isPresent();
        if (isDuplicate) return Response.status(Status.CONFLICT).entity("Purchase record already exists!").build();
        if (purchase.supplier == null || purchase.supplier.id == null)
            return Response.status(Status.BAD_REQUEST).entity("Invalid supplier").build();

        return Supplier.findByIdOptional(purchase.supplier.id).map(supplier -> {
            Purchase.persist(purchase);
            URI purchaseURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(purchase.id)).build();
            return Response.created(purchaseURI).build();
        }).orElseGet(() -> Response.status(Status.NOT_FOUND).entity("Supplier dont exists").build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing purchase record")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Purchase record has been successfully updated"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Purchase record to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Computer.class)),
                    description = "Purchase record payload is not the same as an entity object that needed to be updated")
    })
    public Response updatePurchase(
            @Parameter(description = "Purchase record identifier", required = true) @PathParam("id") @NotNull Long purchaseId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Purchase.class)))
            @Valid Purchase purchase) {
        if (!purchaseId.equals(purchase.id)) return Response.status(Response.Status.CONFLICT).entity(purchase).build();
        else if (purchase.supplier == null)
            return Response.status(Status.BAD_REQUEST).entity("Supplier details should be encluded").build();

        return Purchase.findByIdOptional(purchaseId).map(exists -> {
            Panache.getEntityManager().merge(purchase);
            return Response.status(Status.NO_CONTENT).build();
        }).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing purchase record")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Purchase record has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Purchase record to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Purchase record not found")
    })
    public Response deletePurchase(
            @Parameter(description = "Purchase record identifier", required = true) @PathParam("id") @NotNull Long purchaseId) {
        return Purchase.deleteById(purchaseId)
                ? Response.status(Status.NO_CONTENT).build()
                : Response.status(Status.NOT_FOUND).build();
    }
}
