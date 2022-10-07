package com.assets.management.electronic.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.assets.management.electronic.model.Computer;
import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.service.DeviceService;

@Path("/devices")
public class DeviceResource {

	@Inject
	Logger LOG;

	@Inject
	DeviceService deviceService;

	@POST
	@Path("/phone")
	@Produces("image/png")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSmartPhone(@Valid SmartPhone phone,
			@Context UriInfo uriInfo) {

		SmartPhone qrStream; // = null;
		try {
			qrStream = deviceService.persistPhone(phone);
		} catch (NoResultException nre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException nur) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		URI uri = uriInfo.getAbsolutePathBuilder().path(
				Long.toString(phone.id)
		).build();
		return Response.created(uri).entity(qrStream).build();

	}

	@POST
	@Path("/qr/computer")
	@Produces("image/png")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComputer(@Valid Computer computer,
			@Context UriInfo uriInfo) {
		byte[] qrStream; // = null;
		try {
			qrStream = deviceService.persistComputer(computer);
		} catch (Exception rollbackException) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

//		URI createdUri = uriInfo.getAbsolutePathBuilder().path(
//				Long.toString(computer.id)
//		).build();
		return Response.created(URI.create(Long.toString(computer.id))).entity(
				qrStream
		).build();

	}
}
