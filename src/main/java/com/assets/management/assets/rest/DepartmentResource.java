package com.assets.management.assets.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.assets.management.assets.model.Department;
import com.assets.management.assets.service.DepartmentService;

@Path("/departments")
public class DepartmentResource {

	@Inject
	Logger LOG;

	@Inject
	DepartmentService departmentService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response findAllDepartments(@QueryParam("name") String deptName) {
		if (deptName == null)
			return Response.ok(Department.findAllOrderByName()).build();

		return Department.findByName(deptName).map(
		        department -> Response.ok(department).build()
		).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	} 

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findDepartmentById(@PathParam("id") @NotNull Long deptId) {
		LOG.debug("DEPT ID FROM TEST : " + deptId);
		return departmentService.findDepartment(deptId).map(
		        department -> Response.ok(department).build()
		).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	} 

	
	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countDepartment() { 
		return Response.ok(Department.count()).build();
	} 
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDepartment(
	        @Valid Department department, @Context UriInfo uriInfo
	) {
		URI departmentUri = departmentService.insertDepartment(
		        department, uriInfo
		);
		return Response.created(departmentUri).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDepartment(
	        @PathParam("id") @NotNull Long deptId, @Valid Department department
	) {

		if (!deptId.equals(department.id))
			return Response.status(Response.Status.CONFLICT).entity(
			        department
			).build();

		try {
			departmentService.updateDepartment(department, deptId);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteDepartment(@PathParam("id") @NotNull Long id) {
		try {
			departmentService.deleteDepartment(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
}
