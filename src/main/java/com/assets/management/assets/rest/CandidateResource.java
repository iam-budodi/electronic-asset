package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.EndUser;
import com.assets.management.assets.service.CandidateService;

@Path("/candidates")
public class CandidateResource {

	@Inject
	Logger LOG;

	@Inject
	CandidateService candidateService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCandidate(
	        @Valid EndUser endUser,
	        @Context UriInfo uriInfo
	) {
		URI uri;
		try {
			uri = candidateService.createCandidate(endUser, uriInfo);
		} catch (IllegalArgumentException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.created(uri).build();
	}

	@PUT
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignAsset(
	        @PathParam("id") Long candidateId,
	        @Valid Asset asset
	) {
		LOG.info("Check Asset: " + asset);

		if (asset.id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (asset.endUser != null)
			return Response.status(Status.CONFLICT).build();

		try {
			candidateService.assignAsset(asset, candidateId);
		} catch (IllegalArgumentException | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllCandidateAssets(@PathParam("id") Long candidateId) {
		List<Asset> assets = candidateService.getAllAssets(candidateId);
		return Response.ok(assets).build();
	}

	@DELETE
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unAssignAsset(
	        @PathParam("id") Long candidateId,
	        @QueryParam("sn") @NotNull String serialNumber
	) {
		try {
			candidateService.unAssignAsset(candidateId, serialNumber);
		} catch (IllegalArgumentException | NotFoundException bre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}
