package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.service.DepartmentService;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@Path("/departments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="Department Endpoint", description = "This API allows to organize employees into specific departments ")
public class DepartmentResource {

	@Inject
	Logger LOG;

	@Inject
	DepartmentService departmentService;

	@GET
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Retrieves all available deparments from the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class, type = SchemaType.ARRAY)),
				description = "Lists all the departments"),
		@APIResponse(responseCode = "204", description = "No department to display"),
		@APIResponse(responseCode = "404", description = "Department is not found for a given name query")
	})
	public Response allDepartments(@Parameter(description = "Department name query parameter", required = false) @QueryParam("name") String deptName) {
		List<Department> departments = Department.findAllOrderByName();
		if (departments.size() == 0) return Response.status(Status.NO_CONTENT).build();
		if (deptName == null) return Response.ok(departments).build();

		return Department.findByName(deptName)
				.map(department -> Response.ok(department).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Returns the department for a given identifier")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
				description = "Returns a found department"),
		@APIResponse(responseCode = "400", description = "Invalid input"),                       
		@APIResponse(responseCode = "404", description = "Department is not found for a given identifier")
	})
	public Response findDepartment(@Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId) {
		return departmentService.findDepartment(deptId)
				.map(department -> Response.ok(department).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Counts all departments available in the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Long.class)),
				description = "Number of all departments available"),
		@APIResponse(responseCode = "204", description = "No department available in the database")
	})
	public Response countDepartment() {
		Long nbDepartment = Department.count();
		if (nbDepartment == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(nbDepartment).build();
	}

	@POST
	@Operation(summary = "Creates a valid department and stores it into the database")
	@APIResponses({
		@APIResponse(
				responseCode = "201", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
				description = "URI of the created department"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(
				responseCode = "409", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
				description = "Department duplications is not allowed")
	})
	public Response createDepartment(
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class))) 
			@Valid Department department, @Context UriInfo uriInfo) {
		boolean isDept = Department.findByName(department.name).isPresent();
		if (isDept) return Response.status(Status.CONFLICT).entity("Department already exists").build();
		departmentService.insertDepartment(department);
		URI deptUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(department.id)).build();
		return Response.created(deptUri).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Updates an existing department")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Department has been successfully updated"),
		@APIResponse(responseCode = "404", description = "Department to be updated does not exist in the database"),
		@APIResponse(responseCode = "415", description = "Format is not JSON"),
		@APIResponse(
				responseCode = "409", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class)),
				description = "Department payload is not the same as an entity object that needed to be updated")
	})
	public Response updateDepartment(
			@Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId, 
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Department.class))) 
			@Valid Department department) {
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
	@Transactional(Transactional.TxType.REQUIRED)
	@Operation(summary = "Deletes an existing department")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Department has been successfully deleted"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(responseCode = "404", description = "Department to be deleted does not exist in the database"),
		@APIResponse(responseCode = "500", description = "Department not found")
	})
	public Response deleteDepartment(@Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long deptId) {
		return Department.deleteById(deptId) 
				? Response.status(Status.NO_CONTENT).build() 
						: Response.status(Status.NOT_FOUND).build();
	}
	
	@GET
	@Path("/{id}/employees")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Retrieves all employees available in a specific deparment given an identifier")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
				description = "Lists all employees from the department identified by the department identifier"),
		@APIResponse(responseCode = "204", description = "No employee in a specified department"),
		@APIResponse(responseCode = "404", description = "Employee does not exist for a given work identifier query")
	})
	public Response listAllEmployeeFromDepartment(
			@Parameter(description = "Department identifier", required = true) @PathParam("id") @NotNull Long departmentId, 
			@Parameter(description = "Employee work identifier", required = false) @QueryParam("workid") String workId) {
//		String queryString = "FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address ";
//		List<Employee> employees = Employee.find(queryString + "WHERE d.id = ?1", 
//				Sort.by("e.firstName").and("e.lastName"), 
//				departmentId).list();

//		if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
//		if (workId == null) return Response.ok(employees).build();
 
//		return Employee.find(queryString + "WHERE e.workId LIKE :workId",
//				Parameters.with("workId", "%" + workId + "%"))
//				.firstResultOptional()
//				.map(employee -> Response.ok(employee).build())
//				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
		
//		TODO: TRIAL.... delete the commented when this is fully tested 
		PanacheQuery<Employee> employeeQuery = Employee.find(
				"FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address "
				+ "WHERE d.id = :departmentId AND (:workId IS NULL OR e.workId = :workId)", 
				Sort.by("e.firstName").and("e.lastName"), 
				Parameters.with("departmentId", departmentId).and("workId", "%" + workId + "%"));
		
		List<Employee> employees = employeeQuery.list();
		if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
		if (workId == null) return Response.ok(employees).build();
		
		return employeeQuery.firstResultOptional()
				.map(employee -> Response.ok(employee).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
}
