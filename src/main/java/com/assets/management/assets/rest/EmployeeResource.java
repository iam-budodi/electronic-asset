package com.assets.management.assets.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Label;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.QRGenerator;
import com.google.zxing.WriterException;

import io.quarkus.panache.common.Parameters;

@Path("/employees")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeResource {

	@Inject
	Logger LOG;

	@Inject
	EmployeeService employeeService;
	
	@Inject
	QRGenerator qrGenerator;
	
//	@Inject
//	@RestClient
//	QrProxy qrProxy;

	@GET
	public Response listEmployees(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Employee> employees = employeeService.listEmployees(pageIndex, pageSize);
		
		if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(employees).build();
	}

	@GET
	@Path("/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response findEmployee(@PathParam("id") @NotNull Long empId) {
		return Employee.find("FROM Employee e "
				+ "LEFT JOIN FETCH e.department "
				+ "LEFT JOIN FETCH e.address "
				+ "WHERE e.id = :id ", 
				Parameters.with("id", empId))
				.firstResultOptional()
				.map(employee -> Response.ok(employee).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response countEmployees() {
		Long nbEmployees = Employee.count();
		if (nbEmployees == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(nbEmployees).build();
	}

	@POST
	public Response createEmployee(@Valid Employee employee, @Context UriInfo uriInfo) {
		if (employee.address == null || employee.department == null)
			return Response.status(Status.BAD_REQUEST).build();
		else if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
			return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
		else if (!Department.findByIdOptional(employee.department.id).isPresent())
			return Response.status(Status.NOT_FOUND).entity("Department dont exists").build();
 
		employee = employeeService.addEmployee(employee);
		URI employeeUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(employee.id)).build();
		return Response.created(employeeUri).build();
	}
	
	@POST
	@Path("/{id}/allocations")
	@Transactional(Transactional.TxType.REQUIRED)
	public Response allocateAsset(
			@PathParam("id") @NotNull Long employeeId, 
			@Valid Allocation allocation,
			@Context UriInfo uriInfo) throws WriterException, IOException {

		if (allocation.asset== null || allocation.asset.id == null)
			return Response.status(Status.BAD_REQUEST).build();
		else if (allocation.employee != null && !employeeId.equals(allocation.employee.id)) 
			return Response.status(Response.Status.CONFLICT).entity(allocation.employee).build();
		
		Optional<Allocation>  allocated =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset c "
				+ "WHERE c.id = :assetId "
				+ "AND a.status <> :status", 
				Parameters.with("assetId", allocation.asset.id)
				.and("status", AllocationStatus.DEALLOCATED))
				.firstResultOptional();

		if (allocated.isPresent()) 
			return Response.status(Status.CONFLICT).entity("Asset is already taken!").build();

		LOG.info("THEN TO THE USER ID : " + employeeId);
		Employee employee = Employee.findById(employeeId);
		Asset asset =  Asset.findById(allocation.asset.id);
		
		if (employee == null || asset == null) 
			return Response.status(Response.Status.NOT_FOUND).entity("Employee/Asset don't exist").build();
		
		allocation.employee = employee;
		allocation.asset = asset;
		Allocation.persist(allocation);
		URI allocationURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)) .build();
		
		Label label = new Label();
		label.qrByteString = qrGenerator.generateQrString(allocationURI);
		Label.persist(label);
		asset.label = label; 

		return Response.created(allocationURI).build();
	}
	
	@GET
	@Path("{id}/allocations")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeAllAllocatedAssets(
			@PathParam("id") @NotNull Long employeeId,
			@QueryParam("status") AllocationStatus filteredStatus) {
		
		List<Allocation> allocations = Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH e.department "
				+ "LEFT JOIN FETCH e.address "
				+ "LEFT JOIN FETCH a.asset ast "
				+ "LEFT JOIN FETCH ast.category "
				+ "LEFT JOIN FETCH ast.label "
				+ "LEFT JOIN FETCH ast.purchase p "
				+ "LEFT JOIN FETCH p.supplier s "
				+ "LEFT JOIN FETCH s.address "
				+ "WHERE e.id = :employeeId "
				+ "AND (:status IS NULL OR a.status = :status) ",
				Parameters.with("employeeId", employeeId) 
				.and	("status", filteredStatus)) 
				.list();
		
		if (allocations.size() == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(allocations).build();
	}


	// TODO: implement the resource to redirect when the qr code is scanned and the address clicked
	@GET
	@Path("/{employeeId}/allocations/{allocationId}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response getEmployeeAllocateAsset(
			@PathParam("employeeId") @NotNull Long employeeId,
			@PathParam("allocationId") @NotNull Long allocationId) {
		Optional<Allocation>  allocation =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH e.department "
				+ "LEFT JOIN FETCH e.address "
				+ "LEFT JOIN FETCH a.asset ast "
				+ "LEFT JOIN FETCH ast.category "
				+ "LEFT JOIN FETCH ast.label "
				+ "LEFT JOIN FETCH ast.purchase p "
				+ "LEFT JOIN FETCH p.supplier s "
				+ "LEFT JOIN FETCH s.address "
				+ "WHERE e.id = :employeeId "
				+ "AND a.id = :allocationId ", 
				Parameters.with("employeeId", employeeId)
				.and("allocationId", allocationId))
				.firstResultOptional();
		
		return allocation.map(foundAllocation -> Response.ok(foundAllocation).build())
										.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
		
	@GET
	@Path("{employeeId}/assets/{assetId}") 
	@Produces("image/png")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeQRPreview(
			@PathParam("employeeId") @NotNull Long employeeId,
			@PathParam("assetId") @NotNull Long assetId) {
		Optional<Label> label = Allocation.find("SELECT DISTINCT a.asset.label FROM Allocation a "
				+ "WHERE a.employee.id = :employeeId "
				+ "AND a.asset.id = :assetId "
				+ "AND a.status <> :status", 
				Parameters.with("employeeId", employeeId) 
				.and("status", AllocationStatus.DEALLOCATED)
				.and("assetId", assetId))
				.firstResultOptional();
		
		return label.map(qrImage -> Response.ok(qrImage.qrByteString).build())
							.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
		
	@PUT
	@Path("/{id}")
	public Response updateEmployee(@PathParam("id") @NotNull Long empId, @Valid Employee employee) {
		if (!empId.equals(employee.id)) return Response.status(Response.Status.CONFLICT).entity(employee).build();
		else 	if (employee.department == null) return Response.status(Status.BAD_REQUEST).build();

		try {
			employeeService.updateEmployee(employee, empId);
		} catch (EntityNotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteEmployee(@PathParam("id") @NotNull Long empId) {
		try {
			employeeService.deleteEmployee(empId);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
}
