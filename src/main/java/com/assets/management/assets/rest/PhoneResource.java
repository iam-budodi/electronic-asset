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

import com.assets.management.assets.model.Phone;
import com.assets.management.assets.service.PhoneService; 

@Path("/")
public class PhoneResource {

	@Inject
	Logger LOG;

	@Inject
	PhoneService phoneService;

	// mark to delete
	@POST
	@Path("/vendor/{id}/phones")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPhone(
	        @PathParam("id") Long vendorId,
	        @Valid Phone phone,
	        @Context UriInfo uriInfo
	) {
		LOG.info("Check Phone: " + phone);
//		Phone newPhone; // = null;
		try {
			phoneService.persistPhone(phone, vendorId);
		} catch (NoResultException | NotFoundException nfe) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
//
//		if (newPhone == null)
//			return Response.status(Response.Status.NOT_FOUND).build();

		URI uri = uriInfo.getAbsolutePathBuilder()
		        .path(Long.toString(phone.id)).build();
		return Response.created(uri).build();
	}

	@PUT
	@Path("/phones/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePhone(
	        @PathParam("id") @NotNull Long id,
	        @Valid Phone phone
	) {

		if (phone == null || id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!id.equals(phone.id))
			return Response.status(Response.Status.CONFLICT).entity(phone)
			        .build();

		try {
			phoneService.updatePhone(phone, id);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException nur) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Status.NO_CONTENT).build();

	}

	// mark to delete
	@GET
	@Path("/vendor/{id}/phones")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllPhonesByVendor(
	        @PathParam("id") Long vendorId,
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<Phone> phones = phoneService
		        .allPhonesByVendor(vendorId, pageIndex, pageSize);

		return Response.ok(phones).build();
	}

	@GET
	@Path("/phones/total")
	@Produces(MediaType.APPLICATION_JSON)
	public Response totalPhonesCount() {
		return Response.ok(phoneService.countAllPhones()).build();
	}
	
	@GET
	@Path("/phones/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countPhonesPerStatus() {
		return Response.ok(phoneService.countPhonesPerStatus()).build();
	}

	@GET
	@Path("/phones/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findPhoneByVendorId(@PathParam("id") @NotNull Long id) {
		Phone phone;
		try {
			phone = phoneService.findPhoneById(id);
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(phone).build();
	}

	@DELETE
	@Path("/phones/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePhone(@PathParam("id") @NotNull Long id) {
		try {
			phoneService.deletePhone(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
	

	// mark to delete
	@DELETE
	@Path("/vendor/{id}/phones")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllPhonesByVendor(
	        @PathParam("id") Long vendorId
	) {
		Long numberOfDeletedPhones = phoneService
		        .deleteAllPhone(vendorId);

		return Response.ok(numberOfDeletedPhones).build();
	}

}
