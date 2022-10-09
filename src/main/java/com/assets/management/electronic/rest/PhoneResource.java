package com.assets.management.electronic.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.service.PhoneService;

@Path("/devices/phone")
public class PhoneResource {

	@Inject
	Logger LOG;

	@Inject
	PhoneService phoneService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSmartPhone(
	        @Valid SmartPhone phone,
	        @Context UriInfo uriInfo
	) {

		SmartPhone qrStream; // = null;
		try {
			qrStream = phoneService.persistPhone(phone);
		} catch (NoResultException nre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException nur) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		LOG.info("Check Phone Properties: " + qrStream);
		URI uri = uriInfo.getAbsolutePathBuilder()
		        .path(Long.toString(qrStream.id)).build();
		return Response.created(uri).build();

	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSmartPhone(
	        @PathParam("id") Long id,
	        @Valid SmartPhone phone
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllPhones(
	        @QueryParam("page") @DefaultValue("0") Integer pageIndex,
	        @QueryParam("size") @DefaultValue("15") Integer pageSize
	) {
		List<SmartPhone> phones = phoneService
		        .listAllPhones(pageIndex, pageSize);

		return Response.ok(phones).build();
	}

}
