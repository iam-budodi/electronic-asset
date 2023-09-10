package com.japhet_sebastian.supplier;

import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
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

import static com.japhet_sebastian.supplier.SupplierResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Supplier Endpoint", description = "Supplier related operation")
public class SupplierResource extends AbstractSupplierType {

    public static final String RESOURCE_PATH = "/suppliers";

    @Inject
    SupplierService supplierService;

    @Inject
    SecurityContext keycloakSecurityContext;

    @GET
    @Operation(summary = "Get all available suppliers")
    @APIResponse(
            responseCode = "200",
            description = "Lists all the suppliers",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SupplierDto.class, type = SchemaType.ARRAY)))
    public Response listSuppliers(@BeanParam SupplierPage supplierPage) {
        return Response.ok(supplierService.listSuppliers(supplierPage))
                .header("X-Total-Count", supplierService.supplierCount())
                .build();
    }

    @GET
    @Path("/{supplierId}")
    @Operation(summary = "Get supplier for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get supplier by supplierId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SupplierDto.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "Supplier does not exist",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response getSupplier(@Parameter(description = "supplierId", required = true) @PathParam("supplierId") @NotNull String supplierId) {
        return supplierService.findSupplier(supplierId)
                .map(supplier -> Response.ok(supplier).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Get all selection options projection of the suppliers")
    @APIResponse(
            responseCode = "200",
            description = "Get key value pair representation",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)))
    public Response supplierSelectOptions() {
        return Response.ok(supplierService.selectOptions()).build();
    }

    @POST
    @Operation(summary = "Creates valid supplier")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "URI of the created supplier",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Email/phone number is taken",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response createSupplier(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SupplierDto.class))) @Valid SupplierDto supplier,
            @Context UriInfo uriInfo) {
        supplier.setRegisteredBy(keycloakSecurityContext.getUserPrincipal().getName());
        supplierService.saveSupplier(supplier);
        URI supplierURI = supplierUriBuilder(supplier.supplierId, uriInfo).build();
        return Response.created(supplierURI).build();
    }

    @PUT
    @Path("/{supplierId}")
    @Operation(summary = "Updates existing supplier")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Supplier updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SupplierDto.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "400",
                    description = "No supplier found for supplierId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Supplier object does not have supplierId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(
                    responseCode = "400",
                    description = "Path variable supplierId does not match Supplier.supplierId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response updateSupplier(
            @Parameter(description = "supplierId", required = true) @PathParam("supplierId") @NotNull String supplierId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SupplierDto.class))) @Valid SupplierDto supplierDto) {
        if (Objects.isNull(supplierDto.supplierId) || supplierDto.supplierId.isEmpty())
            throw new ServiceException("Supplier object does not have supplierId");

        if (!Objects.equals(supplierId, supplierDto.supplierId))
            throw new ServiceException("path variable supplierId does not match Supplier.SupplierId");

        supplierDto.setUpdatedBy(keycloakSecurityContext.getUserPrincipal().getName());
        supplierService.updateSupplier(supplierDto);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{supplierId}")
    @Operation(summary = "Deletes existing supplier")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Supplier deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "404",
                    description = "Supplier does not exist for a given supplierId",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response deleteCollege(
            @Parameter(description = "supplierId", required = true) @PathParam("supplierId") @NotNull String supplierId) {
        supplierService.deleteSupplier(supplierId);
        return Response.noContent().build();
    }

}
