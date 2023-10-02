package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.vo.AbstractGenericType;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.inject.Inject;
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
import java.util.Objects;

import static com.japhet_sebastian.procurement.purchase.PurchaseResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Purchase Endpoint", description = "Tracks all purchases")
public class PurchaseResource extends AbstractGenericType {
    public static final String RESOURCE_PATH = "/purchases";

    @Context
    UriInfo uriInfo;

    @Inject
    PurchaseService purchaseService;

    @GET
    @Operation(summary = "Get all purchases")
    @APIResponse(
            responseCode = "200",
            description = "Lists purchases",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = PurchaseDto.class, type = SchemaType.ARRAY)))
    public Response listPurchases(@BeanParam PurchasePage purchasePage) {
        return Response.ok(purchaseService.allPurchases(purchasePage))
                .header("X-Total-Count", purchaseService.collegesCount())
                .build();
    }

    @GET
    @Path("/{purchaseId}")
    @Operation(summary = "Get purchase by purchaseId")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get purchase",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PurchaseDto.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "Purchase does not exist for a given purchaseId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response findPurchase(
            @Parameter(description = "purchaseId", required = true) @PathParam("purchaseId") @NotNull String purchaseId) {
        return purchaseService.getPurchase(purchaseId)
                .map(purchase -> Response.ok(purchase).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Operation(summary = "Get all purchases projection")
    @APIResponse(
            responseCode = "200",
            description = "ID and invoice number for the purchases",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)))
    public Response selectOptions() {
        return Response.ok(purchaseService.projection()).build();
    }

//    @GET
//    @Path("/dashboard")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Fetch only purchase date and quantity for dashboard charts")
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "200",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PurchaseChart.class, type = SchemaType.ARRAY)),
//                    description = "Purchase date and quantity for dashboard charts"),
//            @APIResponse(responseCode = "204", description = "No purchase available in the database")
//    })
//    public Response purchaseChart() {
//        List<PurchaseChart> purchaseChart = Purchase.find("SELECT p.purchaseQty, p.purchaseDate FROM Purchase p")
//                .project(PurchaseChart.class).list();
//        if (purchaseChart.size() == 0) return Response.status(Status.NO_CONTENT).build();
//        return Response.ok(purchaseChart).build();
//    }

//    @GET
//    @Path("/{invoice}/assets")
//    @Operation(summary = "Get all assets per invoice")
//            @APIResponse(
//                    responseCode = "200",
//                    description = "Lists assets per invoice",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON,
//                            schema = @Schema(implementation = Asset.class, type = SchemaType.ARRAY))),
//    public Response listAllAssetsPerPurchase(@BeanParam PurchasePage purchasePage) {
//        List<Asset> assets = Asset.getAssetByInvoice(invoiceNumber).page(pIndex, pSize).list();
//        if (assets.size() == 0) return Response.status(Status.NO_CONTENT).build();
//        return Response.ok(assets).build();
//    }

    @POST
    @Operation(summary = "Creates purchase record")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Purchase record created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class, type = SchemaType.STRING))),
            @APIResponse(
                    responseCode = "409",
                    description = "Duplicate is not allowed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PurchaseDto.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Supplier for the purchase does not exist",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response makePurchase(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = PurchaseEntity.class))) @Valid PurchaseDto purchase, @Context UriInfo uriInfo) {
        if (Objects.isNull(purchase.supplier) || Objects.isNull(purchase.supplier.getSupplierId()))
            throw new ServiceException("Invalid supplier");

        purchaseService.savePurchase(purchase);
        URI purchaseURI = purchaseUriBuilder(purchase.purchaseId, uriInfo).build();
        return Response.created(purchaseURI).build();
    }

    @PUT
    @Path("/{purchaseId}")
    @Operation(summary = "Updates purchase record")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Purchase record is updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PurchaseDto.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "Purchase record does not exist",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Purchase does not have purchaseId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Path variable purchaseId does not match Purchase.purchaseId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
    })
    public Response updatePurchase(
            @Parameter(description = "purchaseId", required = true) @PathParam("purchaseId") @NotNull String purchaseId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = PurchaseDto.class))) @Valid PurchaseDto purchase) {
        if (Objects.isNull(purchase.supplier) || Objects.isNull(purchase.supplier.getSupplierId()))
            throw new ServiceException("Invalid input");

        if (Objects.isNull(purchase.purchaseId) || purchase.purchaseId.isEmpty())
            throw new ServiceException("Purchase does not have purchaseId");

        if (!Objects.equals(purchaseId, purchase.purchaseId))
            throw new ServiceException("Path variable purchaseId does not match Purchase.purchaseId");

        purchaseService.updatePurchase(purchase);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{purchaseId}")
    @Operation(summary = "Deletes purchase record")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Purchase record deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "404",
                    description = "No purchase found for purchaseId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
    })
    public Response deletePurchase(
            @Parameter(description = "purchaseId", required = true) @PathParam("purchaseId") @NotNull String purchaseId) {
        purchaseService.deletePurchase(purchaseId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
