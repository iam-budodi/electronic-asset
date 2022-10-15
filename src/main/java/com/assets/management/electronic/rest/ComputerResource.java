package com.assets.management.electronic.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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

import com.assets.management.electronic.model.Computer;
import com.assets.management.electronic.service.ComputerService;

@Path("/")
public class ComputerResource {

	@Inject
	Logger LOG;

	@Inject
	ComputerService computerService;

	@POST
	@Path("/vendor/{id}/computers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSmartPhone(
	        @PathParam("id") Long vendorId,
	        @Valid Computer computer,
	        @Context UriInfo uriInfo
	) {
		LOG.info("Check Computer: " + computer);
		try {
			computerService.persistComputer(computer, vendorId);
		} catch (NoResultException | NotFoundException nfe) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
//
//		if (newPhone == null)
//			return Response.status(Response.Status.NOT_FOUND).build();

		URI uri = uriInfo.getAbsolutePathBuilder()
		        .path(Long.toString(computer.id)).build();
		return Response.created(uri).build();
	}

	@PUT
	@Path("/computers/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateComputer(
	        @PathParam("id") @NotNull Long id,
	        @Valid Computer computer
	) {

		if (computer == null || id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!id.equals(computer.id))
			return Response.status(Response.Status.CONFLICT).entity(computer)
			        .build();

		try {
			computerService.updateComputer(computer, id);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException nur) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Status.NO_CONTENT).build();

	}

	@GET
	@Path("/vendor/{id}/computers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllComputersByVendor(
	        @PathParam("id") Long vendorId,
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Computer> computers = computerService
		        .allComputersByVendor(vendorId, pageIndex, pageSize);

		return Response.ok(computers).build();
	}

	@GET
	@Path("/computers/total")
	@Produces(MediaType.APPLICATION_JSON)
	public Response totalComputersCount() {
		return Response.ok(computerService.countAllComputers()).build();
	}

	@GET
	@Path("/computers/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countComputersPerStatus() {
		return Response.ok(computerService.countComputerPerStatus()).build();
	}

	@GET
	@Path("/computers/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findComputerByVendorId(@PathParam("id") @NotNull Long id) {
		Computer computer;
		try {
			computer = computerService.findComputerById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(computer).build();
	}

	@DELETE
	@Path("/computers/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComputer(@PathParam("id") @NotNull Long id) {
		try {
			computerService.deleteComputer(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	@DELETE
	@Path("/vendor/{id}/computers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllComputersByVendor(
	        @PathParam("id") Long vendorId
	) {
		Long deletedComputers = computerService.deleteAllComputer(vendorId);

		return Response.ok(deletedComputers).build();
	}
}
