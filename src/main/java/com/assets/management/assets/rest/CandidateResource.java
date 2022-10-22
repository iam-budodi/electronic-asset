package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import com.assets.management.assets.model.Vendor;
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCandidates(
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<EndUser> candidates = candidateService
		        .getAllCandidates(pageIndex, pageSize);
		return Response.ok(candidates).build();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countCandidates() {
		return Response.ok(candidateService.countCandidates()).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCandidate(@PathParam("id") @NotNull Long id) {
		EndUser candidate;
		try {
			candidate = candidateService.findById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(candidate).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCandidate(
	        @PathParam("id") @NotNull Long id,
	        @Valid EndUser candidate
	) {

		if (candidate == null || id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!id.equals(candidate.id))
			return Response.status(Response.Status.CONFLICT).entity(candidate)
			        .build();

		try {
			candidateService.updateById(candidate, id);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	// TODO: make deleting as hard as possible i.e fails due to foreign key
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCandidate(@PathParam("id") @NotNull Long id) {
		try {
			candidateService.deleteById(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllVendors() {
		return Response.ok(candidateService.deleteAll()).build();
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
