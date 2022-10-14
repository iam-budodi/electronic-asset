package com.assets.management.electronic.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

import com.assets.management.electronic.model.Vendor;
import com.assets.management.electronic.service.VendorService;

@Path("/vendors")
public class VendorResource {

	@Inject
	Logger LOG;

	@Inject
	VendorService vendorService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addVendor(@Valid Vendor vendor, @Context UriInfo uriInfo) {
		vendorService.persistVendor(vendor);
		LOG.info("Check Vendor: " + vendor.id);
		URI uri = uriInfo.getAbsolutePathBuilder()
		        .path(Long.toString(vendor.id)).build();

		return Response.created(uri).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateVendor(
	        @PathParam("id") @NotNull Long id,
	        @Valid Vendor vendor
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

	@GET
//	@Path("/{id}/phones")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allVendor(
	        @PathParam("id") Long vendorId,
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Vendor> vendor = vendorService
		        .allVendor(vendorId, pageIndex, pageSize);
		return Response.ok(vendor).build();
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
		Vendor vendor;
		try {
			vendor = vendorService.findById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(vendor).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePhone(@PathParam("id") @NotNull Long id) {
		try {
			vendorService.deleteVendor(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	@DELETE
//	@Path("/vendors/{id}/phones")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllVendor() {
		Long allDeletedVendors = vendorService.deleteAll();

		return Response.ok(allDeletedVendors).build();
	}
}
