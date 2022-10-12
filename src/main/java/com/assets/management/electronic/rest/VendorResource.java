package com.assets.management.electronic.rest;

import java.net.URI;

import javax.inject.Inject;
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

import com.assets.management.electronic.model.Vendor;
import com.assets.management.electronic.service.VendorService;

@Path("/")
public class VendorResource {

	@Inject
	Logger LOG;

	@Inject
	VendorService vendorService;

	@POST
	@Path("/vendors")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addVendor(@Valid Vendor vendor, @Context UriInfo uriInfo) {
		vendorService.persistVendor(vendor);
		LOG.info("Check Vendor: " + vendor);
		URI uri = uriInfo.getAbsolutePathBuilder()
		        .path(Long.toString(vendor.id)).build();

		return Response.created(uri).build();  
	}
}
