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

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Employee;
import com.assets.management.assets.service.EmployeeService;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EndUserResource {

	@Inject
	Logger LOG;

	@Inject
	EmployeeService endUserService;

	@POST
	public Response createEndUsers(
	        @Valid Employee endUser,
	        @Context UriInfo uriInfo
	) {
		URI uri;
		try {
			uri = endUserService.createCandidate(endUser, uriInfo);
		} catch (IllegalArgumentException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.created(uri).build();
	}

	@GET
//	@Produces(MediaType.APPLICATION_JSON)
	public Response listEndUsers(
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Employee> candidates = endUserService
		        .getAllCandidates(pageIndex, pageSize);
		return Response.ok(candidates).build();
	}

	@GET
	@Path("/count")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response countEndUsers() {
		return Response.ok(endUserService.countCandidates()).build();
	}

	@GET
	@Path("/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response findEndUser(@PathParam("id") @NotNull Long id) {
		Employee candidate;
		try {
			candidate = endUserService.findById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(candidate).build();
	}

	@PUT
	@Path("/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEndUser(
	        @PathParam("id") @NotNull Long id,
	        @Valid Employee candidate
	) {

		if (candidate == null || id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!id.equals(candidate.id))
			return Response.status(Response.Status.CONFLICT).entity(candidate)
			        .build();

		try {
			endUserService.updateById(candidate, id);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	// TODO: make deleting as hard as possible i.e fails due to foreign key
	@DELETE
	@Path("/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEndUser(@PathParam("id") @NotNull Long id) {
		try {
			endUserService.deleteById(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	@DELETE
//	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllEndUsers() {
		return Response.ok(endUserService.deleteAll()).build();
	}

	@PUT
	@Path("/{id}/assets")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignAsset(
	        @PathParam("id") Long candidateId,
	        @Valid Item asset
	) {
		LOG.info("Check Asset: " + asset);

		if (asset.id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

//		if (asset.endUser != null)
//			return Response.status(Status.CONFLICT).build();

		try {
			endUserService.assignAsset(asset, candidateId);
		} catch (IllegalArgumentException | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/assets")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllEndUserAssets(@PathParam("id") Long candidateId) {
		List<Item> assets = endUserService.getAllAssets(candidateId);
		return Response.ok(assets).build();
	}

	@DELETE
	@Path("/{id}/assets")
//	@Produces(MediaType.APPLICATION_JSON)
	public Response unAssignAsset(
	        @PathParam("id") Long candidateId,
	        @QueryParam("sn") @NotNull String serialNumber
	) {
		try {
			endUserService.unAssignAsset(candidateId, serialNumber);
		} catch (IllegalArgumentException | NotFoundException bre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}
