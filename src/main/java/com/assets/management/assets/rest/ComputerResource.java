package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.assets.management.assets.model.Computer;
import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Purchase;
import com.assets.management.assets.service.ComputerService;

@Path("/computers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComputerResource {
	
	@Inject
	ComputerService computerService;

	@POST
	public Response createComputer(@Valid Computer computer, @Context UriInfo uriInfo) {
		boolean exists =  Purchase.findByIdOptional(computer.purchase.id).isPresent();
		if (Computer.checkSerialNumber(computer.serialNumber))
			return Response.status(Status.CONFLICT).entity("Duplicate is not allow!").build();
		else if (!exists) 
			return Response.status(Status.NOT_FOUND).entity("Make sure there's purchase record for the item").build();
		
		Computer.persist(computer);
		URI computerUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(computer.id)).build();
		return Response.created(computerUri).build();
	}
	
	@GET
	public Response listAllComputers(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Computer> computers = Computer.find(
				"SELECT DISTINCT c "
				+ "FROM Computer c "
				+ "LEFT JOIN FETCH c.category "
				+ "LEFT JOIN FETCH c.label "
				+ "LEFT JOIN FETCH c.purchase p "
				+ "ORDER BY p.purchaseDate")
				.page(pageIndex, pageSize).list();
		return Response.ok(computers).build();

	}
	
}
