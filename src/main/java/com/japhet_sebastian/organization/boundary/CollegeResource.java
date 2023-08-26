package com.japhet_sebastian.organization.boundary;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.control.CollegeService;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.vo.PageRequest;
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

import static com.japhet_sebastian.organization.boundary.CollegeResource.RESOURCE_PATH;

@Path(RESOURCE_PATH)
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "College Endpoint", description = "College related operations")
public class CollegeResource extends AbstractCollegeType {

    public static final String RESOURCE_PATH = "/colleges";

    @Inject
    CollegeService collegeService;

    @GET
    @Operation(summary = "Get all available colleges")
    @APIResponse(
            responseCode = "200",
            description = "Lists all the colleges",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CollegeDetail.class, type = SchemaType.ARRAY)))
    public Response allColleges(@BeanParam PageRequest pageRequest) {
        return Response.ok(this.collegeService.listColleges(pageRequest))
                .header("X-Total-Count", this.collegeService.totalColleges())
                .build();
    }

    @GET
    @Path("/{collegeId}")
    @Operation(summary = "Get college for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Get college by college identifier",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CollegeDetail.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "College does not exist for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response getCollege(
            @Parameter(description = "collegeId", required = true)
            @PathParam("collegeId") @NotNull String collegeId) {
        return this.collegeService.getCollege(collegeId)
                .map(collegeDetail -> Response.ok(collegeDetail).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Operation(summary = "Get all selection options projection of the college")
    @APIResponse(
            responseCode = "200",
            description = "Return an object with identifier and name as key value pair",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY))
    )
    public Response selectOptions() {
        return Response.ok(this.collegeService.selected()).build();
    }

    @POST
    @Operation(summary = "Creates valid college")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "College created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = URI.class, type = SchemaType.STRING))),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "College already exists for college identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response createCollege(@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = CollegeDetail.class, type = SchemaType.OBJECT)))
                                  @Valid CollegeDetail collegeDetail, @Context UriInfo uriInfo) {
        this.collegeService.addCollege(collegeDetail);
        URI collegeURI = collegeUriBuilder(collegeDetail.getCollegeId(), uriInfo).build();
        return Response.created(collegeURI).build();
    }

    @PUT
    @Path("/{collegeId}")
    @Operation(summary = "Updates existing college")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "College updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CollegeDetail.class, type = SchemaType.OBJECT))),
            @APIResponse(
                    responseCode = "404",
                    description = "No College found for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid object",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "College object does not have collegeId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "400",
                    description = "Path variable collegeId does not match College.collegeId",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
    })
    public Response updateCollege(
            @Parameter(description = "College identifier", required = true) @PathParam("collegeId") @NotNull String collegeId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CollegeDetail.class, type = SchemaType.OBJECT)))
            @Valid CollegeDetail collegeDetail) {
        if (Objects.isNull(collegeDetail.getCollegeId()) || collegeDetail.getCollegeId().isEmpty())
            throw new ServiceException("College does not have collegeId");

        if (!Objects.equals(collegeId, collegeDetail.getCollegeId()))
            throw new ServiceException("path variable collegeId does not match College.collegeId");

        collegeService.updateCollege(collegeDetail);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{collegeId}")
    @Operation(summary = "Deletes existing college")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "College deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @APIResponse(
                    responseCode = "404",
                    description = "College does not exist for a given identifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    public Response deleteCollege(
            @Parameter(description = "collegeId", required = true) @PathParam("collegeId") @NotNull String collegeId) {
        this.collegeService.deleteCollege(collegeId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
