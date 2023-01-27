  package com.assets.management.assets.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.assets.management.assets.model.entity.ItemAssignment;
import com.assets.management.assets.model.entity.TransferHistory;
import com.assets.management.assets.service.TransferService;

@Path("transfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {
	
	@Inject
	TransferService transferService;
	
	@POST
	public Response transferItem(
			@QueryParam("item") @NotNull Long itemId, 
			@QueryParam("employee-id") @NotNull Long transToId,
			@Valid TransferHistory history, 
			@Context UriInfo uriInfo) {
 
		if (!ItemAssignment.isItemAssigned(itemId))
			return Response.status(Status.NOT_FOUND)
					.entity("Please assign item to Employee!").build();

		try {
			history = transferService.transferItem(history, itemId, transToId);
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		URI uri = uriInfo
				.getAbsolutePathBuilder()
				.path(Long.toString(history.id))
				 .build();

		return Response.created(uri).build();
	}
}
