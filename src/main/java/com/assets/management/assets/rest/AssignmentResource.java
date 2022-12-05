package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.ItemAssignment;
import com.assets.management.assets.service.AssignmentService;

@Path("/assigns")
public class AssignmentResource {

	@Inject
	AssignmentService assignmentService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignItem(
			@QueryParam("emp") @NotNull Long employeeId, 
			@QueryParam("item") @NotNull Long itemId,
			@Valid ItemAssignment assignment, 
			@Context UriInfo uriInfo) {

		if (ItemAssignment.checkIfAssigned(itemId))
			return Response.status(Status.CONFLICT).entity("Item already taken!").build();

		try {
			assignment = assignmentService.assignItem(assignment, employeeId, itemId);
		} catch (NotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(assignment.id)).build();

		return Response.created(uri).build();
	}

	@GET
	@Path("/{id}/items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listEmployeeItems(@PathParam("id") Long empId) {
		if (!ItemAssignment.checkIfExists(empId))
			return Response.status(Status.NOT_FOUND).build();
		
		List<Item> items = assignmentService.getAssignedItems(empId);
		return Response.ok(items).build();
	}

	@DELETE
	@Path("/{id}/items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response unassignItem(
			@PathParam("id") Long empId, 
			@QueryParam("sn") @NotNull String serialNo) {
		try {
			assignmentService.unassignItem(empId, serialNo);
		} catch (IllegalArgumentException | NotFoundException bre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}
