package com.assets.management.assets.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

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

import com.assets.management.assets.model.Department;
import com.assets.management.assets.model.Employee;
import com.assets.management.assets.service.DepartmentService;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

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

		return Department
				.findByName(deptName)
				.map(department -> Response.ok(department).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findDept(@PathParam("id") @NotNull Long deptId) {
		return departmentService
				.findDepartment(deptId)
				.map(department -> Response.ok(department).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countDepartment() {
		return Response.ok(Department.count()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDepartment(@Valid Department department, @Context UriInfo uriInfo) {
		boolean exists = Department.find(
				"#Department.getName", Parameters.with("name", department.name)
				.map()).firstResultOptional()
				.isPresent();

		if (exists) return Response.status(Status.CONFLICT).entity("Department already exists").build();
		department = departmentService.insertDepartment(department);
		URI deptUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(department.id)).build();
		return Response.created(deptUri).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDepartment(
			@PathParam("id") @NotNull Long deptId, @Valid Department department) {
		if (!deptId.equals(department.id))
			return Response.status(Response.Status.CONFLICT).entity(department).build();

		try {
			departmentService.updateDepartment(department, deptId);
		} catch (NotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDepartment(@PathParam("id") @NotNull Long id) {
		try {
			departmentService.deleteDepartment(id);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
	

	@GET
	@Path("/{id}/employees")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response listAllEmployeeFromDepartment(
			@PathParam("id") @NotNull Long departmentId, @QueryParam("workid") String workId) {
		if (workId == null)
			return Response.ok(Employee.list("department.id = ?1", Sort.by("firstName").and("lastName"), departmentId)).build();

		// if it fails try position ?1 parameter instead of named param :workId
		return Employee.find("workId LIKE :workId", Parameters.with("workId", "%" + workId + "%")).firstResultOptional()
				.map(employee -> Response.ok(employee).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@DELETE
	@Path("/{id}/employee")
	@Transactional(Transactional.TxType.REQUIRED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEmployeeFromDepartment(@PathParam("id") @NotNull Long departmentId,
			@QueryParam("empid") Long employeeId) {
		if (employeeId == null)
			return Response.status(BAD_REQUEST).entity("Specify identity of the employee to be deleted").build();

		boolean deleted = Employee.deleteById(employeeId);
		if (!deleted) return Response.status(Status.NOT_FOUND).build();
		return Response.noContent().build();
	}
}
