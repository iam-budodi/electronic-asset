package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Transfer;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("preview")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Transactional(Transactional.TxType.SUPPORTS)
public class QRPreviewResource {

    @GET
    @Produces("image/png")
    @Path("{employeeId}/allocation")
    @Operation(summary = "Previews the QR Code image")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = "image/png",
                    schema = @Schema(implementation = String.class, format = "binary")),
                    description = "QR code image"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    public Response preview(
            @Parameter(description = "Employee Identifier", required = true) @PathParam("employeeId") @NotNull Long employeeId,
            @Parameter(description = "Search by serial number", required = true) @QueryParam("sn") @NotNull String assetSerialNumber
    ) {
        Allocation allocated = Allocation.preview(employeeId, assetSerialNumber);
        Transfer transferred = Transfer.preview(employeeId, assetSerialNumber);

        if (allocated == null && transferred == null)
            return Response.status(Response.Status.NO_CONTENT).build();

        return allocated == null
                ? Response.ok(transferred.asset.label.qrByteString).build()
                : Response.ok(allocated.asset.label.qrByteString).build();
    }

}
