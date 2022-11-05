package com.assets.management.assets.rest;

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

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Supplier;
import com.assets.management.assets.service.VendorService;

@Path("/vendors")
public class VendorResource {

	@Inject
	Logger LOG;

	@Inject
	VendorService vendorService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createVendor(
	        @Valid Supplier vendor,
	        @Context UriInfo uriInfo
	) {
		URI uri;
		try {
			uri = vendorService.createVendor(vendor, uriInfo);
		} catch (IllegalArgumentException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.created(uri).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listVendors(
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Supplier> vendors = vendorService.getAllVendors(pageIndex, pageSize);
		return Response.ok(vendors).build();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countVendors() {
		return Response.ok(vendorService.countVendors()).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findVendorById(@PathParam("id") @NotNull Long id) {
		Supplier vendor;
		try {
			vendor = vendorService.findById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(vendor).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateVendor(
	        @PathParam("id") @NotNull Long id,
	        @Valid Supplier vendor
	) {

		if (vendor == null || id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!id.equals(vendor.id))
			return Response.status(Response.Status.CONFLICT).entity(vendor)
			        .build();

		try {
			vendorService.updateVendor(vendor, id);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	// TODO: make deleting as hard as possible
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteVendor(@PathParam("id") @NotNull Long id) {
		try {
			vendorService.deleteVendorById(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllVendors() {
		return Response.ok(vendorService.deleteAllVendors()).build();
	}

	@POST
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAsset(
	        @PathParam("id") Long vendorId,
	        @Valid Item asset,
	        @Context UriInfo uriInfo
	) { 
		URI uri;
		try {
			uri = vendorService.addAsset(asset, vendorId, uriInfo);
		} catch (NoResultException | NotFoundException nfe) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException | IllegalArgumentException
		        | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.created(uri).build();
	}

	@GET
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllVendorAssets(
	        @PathParam("id") Long vendorId,
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Item> assets = vendorService
		        .getAllAssets(vendorId, pageIndex, pageSize);

		return Response.ok(assets).build();
	}

	@DELETE
	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllVendorAssets(@PathParam("id") Long vendorId) {
		Long numberOfDeletedAsset;
		try {
			numberOfDeletedAsset = vendorService.deleteAllAssets(vendorId);
		} catch (NotFoundException ex) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return Response.ok(numberOfDeletedAsset).build();
	}
}
