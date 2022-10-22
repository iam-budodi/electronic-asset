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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.EndUser;
import com.assets.management.assets.model.Vendor;
import com.assets.management.assets.service.AssetService;

@Path("/assets")
public class AssetResource {

	@Inject
	Logger LOG;

	@Inject
	AssetService assetService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllAssets(
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Asset> assets = assetService.getAllAssets(pageIndex, pageSize); 
		return Response.ok(assets).build();
	}
	

//	@GET
//	@Path("/count")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response countVendors() {
//		return Response.ok(vendorService.countVendors()).build();
//	}
//
//	@GET
//	@Path("/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response findVendorById(@PathParam("id") @NotNull Long id) {
//		Vendor vendor;
//		try {
//			vendor = vendorService.findById(id);
//		} catch (NotFoundException nfe) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
//		return Response.ok(vendor).build();
//	}
//
//	@PUT
//	@Path("/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateVendor(
//	        @PathParam("id") @NotNull Long id,
//	        @Valid Vendor vendor
//	) {
//
//		if (vendor == null || id == null)
//			return Response.status(Response.Status.BAD_REQUEST).build();
//
//		if (!id.equals(vendor.id))
//			return Response.status(Response.Status.CONFLICT).entity(vendor)
//			        .build();
//
//		try {
//			vendorService.updateVendor(vendor, id);
//		} catch (EntityNotFoundException | NoResultException enf) {
//			return Response.status(Response.Status.NOT_FOUND).build();
//		}
//
//		return Response.status(Status.NO_CONTENT).build();
//	}

//	// WIP
//	@POST
//	@Path("/{id}/candidates")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response assignCandidate(
//	        @PathParam("id") Long assetId,
//	        @Valid EndUser candidate,
//	        @Context UriInfo uriInfo
//	) {
//		LOG.info("Check End USer: " + candidate);
////		Phone newPhone; // = null;
//		try {
//			assetService.persistPhone(phone, vendorId);
//		} catch (NoResultException | NotFoundException nfe) {
//			return Response.status(Response.Status.NOT_FOUND).build();
//		} catch (NonUniqueResultException | BadRequestException bre) {
//			return Response.status(Response.Status.BAD_REQUEST).build();
//		}
////
////		if (newPhone == null)
////			return Response.status(Response.Status.NOT_FOUND).build();
//
//		URI uri = uriInfo.getAbsolutePathBuilder()
//		        .path(Long.toString(phone.id)).build();
//		return Response.created(uri).build();
//	}

//	@PUT
//	@Path("/phones/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updatePhone(
//	        @PathParam("id") @NotNull Long id,
//	        @Valid Phone phone
//	) {
//
//		if (phone == null || id == null)
//			return Response.status(Response.Status.BAD_REQUEST).build();
//
//		if (!id.equals(phone.id))
//			return Response.status(Response.Status.CONFLICT).entity(phone)
//			        .build();
//
//		try {
//			phoneService.updatePhone(phone, id);
//		} catch (EntityNotFoundException | NoResultException enf) {
//			return Response.status(Response.Status.NOT_FOUND).build();
//		} catch (NonUniqueResultException nur) {
//			return Response.status(Response.Status.BAD_REQUEST).build();
//		}
//
//		return Response.status(Status.NO_CONTENT).build();
//
//	}
//
//	@GET
//	@Path("/phones/total")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response totalPhonesCount() {
//		return Response.ok(phoneService.countAllPhones()).build();
//	}
//	
//	@GET
//	@Path("/phones/count")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response countPhonesPerStatus() {
//		return Response.ok(phoneService.countPhonesPerStatus()).build();
//	}
//
//	@GET
//	@Path("/phones/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response findPhoneByVendorId(@PathParam("id") @NotNull Long id) {
//		Phone phone;
//		try {
//			phone = phoneService.findPhoneById(id);
//		} catch (NotFoundException nfe) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
//		return Response.ok(phone).build();
//	}
//
//	@DELETE
//	@Path("/phones/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response deletePhone(@PathParam("id") @NotNull Long id) {
//		try {
//			phoneService.deletePhone(id);
//		} catch (EntityNotFoundException nfe) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
//		return Response.noContent().build();
//	}
//	
//
//	// mark to delete
//	@DELETE
//	@Path("/vendor/{id}/phones")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response deleteAllPhonesByVendor(
//	        @PathParam("id") Long vendorId
//	) {
//		Long numberOfDeletedPhones = phoneService
//		        .deleteAllPhone(vendorId);
//
//		return Response.ok(numberOfDeletedPhones).build();
//	}

}
