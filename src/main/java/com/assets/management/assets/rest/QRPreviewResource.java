package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Transfer;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("preview")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Transactional(Transactional.TxType.SUPPORTS)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "QR Code Preview Endpoint", description = "Displays QR Code image for preview")
public class QRPreviewResource {

    @GET
    @Produces("image/png")
    @Path("{work-id}/allocation")
    @Operation(summary = "Previews the QR Code image")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = "image/png",
                    schema = @Schema(implementation = String.class, format = "binary")),
                    description = "QR code image"),
            @APIResponse(responseCode = "204", description = "Nothing to display"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    public Response preview(
            @Parameter(description = "Work ID", required = true) @PathParam("work-id") @NotNull String workId,
            @Parameter(description = "Search by serial number", required = true) @QueryParam("sn") @NotNull String assetSerialNumber
    ) {
        Allocation allocated = Allocation.preview(workId, assetSerialNumber);
        Transfer transferred = Transfer.preview(workId, assetSerialNumber);

        if (allocated == null && transferred == null)
            return Response.status(Response.Status.NO_CONTENT).build();

        return allocated == null
                ? Response.ok(transferred.asset.label.qrByteString).build()
                : Response.ok(allocated.asset.label.qrByteString).build();

//        if (allocated == null) return Response.ok(transferred.asset.label.qrByteString).build();
//        else if (transferred == null) return Response.ok(allocated.asset.label.qrByteString).build();

    }

}
