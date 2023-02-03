package com.assets.management.assets.rest;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Label;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.QRGenerator;
import com.google.zxing.WriterException;

import io.quarkus.panache.common.Parameters;

@Path("/employees")
public class EmployeeResource {

	@Inject
	Logger LOG;

	@Inject
	EmployeeService employeeService;
	
	@Inject
	QRGenerator qrGenerator;
	
	@Inject
	@RestClient
	QrProxy qrProxy;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listEmployees(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Employee> employees = employeeService.listEmployees(pageIndex, pageSize);
		return Response.ok(employees).build();
	}

	@GET
	@Path("/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.TEXT_PLAIN)
	public Response countEmployees() {
		return Response.ok(Employee.count()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
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
	
	// TODO: BEfore allocating check is the allocation exists with the deallocated status
	@POST
	@Path("/{id}/assets")
	@Transactional(Transactional.TxType.REQUIRED)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response allocateAsset(
			@PathParam("id") @NotNull Long employeeId, 
//			@QueryParam("asset") @NotNull Long assetId, 
			@Valid Allocation allocation,
			@Context UriInfo uriInfo) throws WriterException, IOException {

		if (allocation.asset== null || allocation.asset.id == null)
			return Response.status(Status.BAD_REQUEST).build();
		else if (allocation.employee != null && !employeeId.equals(allocation.employee.id)) 
			return Response.status(Response.Status.CONFLICT).entity(allocation.employee).build();
		
		Optional<Allocation>  allocated =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset c "
				+ "WHERE c.id = :compId "
				+ "AND a.status <> :status", 
				Parameters.with("compId", allocation.asset.id)
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
		
		LOG.info("THEN URI : " + allocationURI.toString());
		// Content for creating QR: http://localhost:8802/rest/employees/6/assets/8
		
		Label label = new Label();
		label.qrByteString = qrGenerator.generateQrString(allocationURI);
		Label.persist(label);
		 asset.label = label; // used to update asset with label qr code when asset was the r/ship owning side 

		return Response.created(allocationURI).build();
	}


	// TODO: Update the method to also look into the transfer table
	@GET
	@Path("{id}/allocations")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeAllocations(
			@PathParam("id") @NotNull Long employeeId,
			@QueryParam("status") AllocationStatus status) {
		
		List<Allocation> allocations = Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset ast "
//				+ "LEFT JOIN FETCH ast.label l " // test the behavior befor uncomment
				+ "WHERE e.id = :employeeId "
				+ "AND a.status <> :status", 
				Parameters.with("employeeId", employeeId) //check before you go further
				.and("status", status)) // ERROR: check status side of this AND condition
				.list();
		
		return Response.ok(allocations).build();
	}
	
	// TODO: Update the method to also look into the transfer table
	@GET
	@Path("{employee-id}/allocations/{asset-id}") 
//	@Path("{id}/allocations") // check this when all other fails
	@Produces("image/png")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response qrImagePreview(
			@PathParam("employee-id") @NotNull Long employeeId,
//			@QueryParam("serialno") @NotNull String serialNumber,
			@PathParam("asset-id") @NotNull Long assetId){
		// TODO: IN THE RELATED GET REQUEST RETURN THE QR CODE OF THE ENCRYPTED  URL OF THE ALLOCATION DETAILS
		
		Optional<Label> label = Allocation.find("SELECT DISTINCT a.asset.label FROM Allocation a "
				+ "WHERE a.employee.id = :employeeId "
				+ "AND a.asset.id = :assetId",
				Parameters.with("employeeId", employeeId) 
				.and("assetId", assetId))
				.firstResultOptional();
		
		return label.map(qrImage -> Response.ok(qrImage.qrByteString).build())
				.orElseGet(() -> Response.status(Status.NO_CONTENT).build());
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
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
