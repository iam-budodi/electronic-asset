package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
import com.assets.management.assets.model.Employee;
import com.assets.management.assets.service.EmployeeService;

@Path("/employees")
public class EmployeeResource {

	@Inject
	Logger LOG;

	@Inject
	EmployeeService employeeService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listEmployees(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Employee> employees = employeeService
				.listEmployees(pageIndex, pageSize);
		return Response.ok(employees).build();
	}

	@GET
	@Path("/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findEmployee(@PathParam("id") @NotNull Long empId) {
		return Employee.findByIdOptional(empId).map(
				employee -> Response.ok(employee).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Produces(MediaType.TEXT_PLAIN)
	public Response countEmployees() {
		return Response.ok(Employee.count()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createEmployee(@Valid Employee employee, @Context UriInfo uriInfo) {
		employee = employeeService.addEmployee(employee);
		URI employeeUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(employee.id)).build();

		return Response.created(employeeUri).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEmployee(@PathParam("id") @NotNull Long empId, @Valid Employee employee) {
		if (employee == null || empId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		if (!empId.equals(employee.id))
			return Response.status(Response.Status.CONFLICT).entity(employee)
					.build();

		try {
			employeeService.updateById(employee, empId);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteEmployee(@PathParam("id") @NotNull Long empId) {
		try {
			employeeService.deleteById(empId);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

	// TODO: make it delete multiple selected employees object
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteAllEndUsers() {
		return Response.ok(employeeService.deleteAll()).build();
	}

	// Move to assignment  API
	@PUT
	@Path("/{id}/assets")
	// @Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response assignAsset(
			@PathParam("id") Long candidateId,
			@Valid Item asset) {
		LOG.info("Check Asset: " + asset);

		if (asset.id == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		// if (asset.endUser != null)
		// return Response.status(Status.CONFLICT).build();

		try {
			employeeService.assignAsset(asset, candidateId);
		} catch (IllegalArgumentException | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{id}/assets")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response listAllEndUserAssets(@PathParam("id") Long candidateId) {
		List<Item> assets = employeeService.getAllAssets(candidateId);
		return Response.ok(assets).build();
	}

	@DELETE
	@Path("/{id}/assets")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response unAssignAsset(
			@PathParam("id") Long candidateId,
			@QueryParam("sn") @NotNull String serialNumber) {
		try {
			employeeService.unAssignAsset(candidateId, serialNumber);
		} catch (IllegalArgumentException | NotFoundException bre) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}
