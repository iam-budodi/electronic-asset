package com.assets.management.assets.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
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

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.QRGenerator;
import com.google.zxing.WriterException;

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
		return employeeService.findById(empId)
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
	@Path("/{id}/allocates")
	@Transactional(Transactional.TxType.REQUIRED)
	public Response allocateAsset(
			@PathParam("id") @NotNull Long employeeId, 
			@Valid Allocation allocation,
			@Context UriInfo uriInfo) {

		if (allocation.asset == null || allocation.asset.id == null)
			return Response.status(Status.BAD_REQUEST).build();
		else if (allocation.employee != null && !employeeId.equals(allocation.employee.id)) 
			return Response.status(Response.Status.CONFLICT).entity(allocation.employee).build();

		URI allocationURI;		
		try {
			employeeService.allocateAsset(allocation, employeeId);
			allocationURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)).build();
			employeeService.updateAssetWithlabel(allocation.asset, allocationURI);
		} catch (NotFoundException nf) {
			return Response.status(Response.Status.NOT_FOUND).entity("Employee/Asset don't exist").build();
		} catch (ClientErrorException ce) {
			return Response.status(Status.CONFLICT).entity("Asset is already taken!").build();
		} catch (WriterException | IOException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		return Response.created(allocationURI).build();
	}
	 
	@POST
	@Path("/{id}/transfers")
	public Response transferAsset(
			@PathParam("id") @NotNull Long fromEmployeeId,
			@Valid Transfer transfer,
			@Context UriInfo uriInfo) {
		if (transfer.asset == null || transfer.asset.id == null)
			return Response.status(Status.BAD_REQUEST).build();
		else if (transfer.fromEmployee == null || !fromEmployeeId.equals(transfer.fromEmployee.id)) 
			return Response.status(Response.Status.CONFLICT).entity(transfer.fromEmployee).build();
		else if (transfer.toEmployee == null || transfer.toEmployee.id == null) 
			return Response.status(Response.Status.BAD_REQUEST).entity("Include employee to transfer the asset").build();

		URI transferURI = null;
		String uriSubPath = "employees" + "/" + Long.toString(transfer.toEmployee.id) + "/" + "allocates" + "/";
		
		try {
			Transfer transfered = employeeService.transferAsset(transfer, fromEmployeeId);
			transferURI = uriInfo.getBaseUriBuilder().path(uriSubPath + Long.toString(transfer.id)).build();
			LOG.info("TRANSFERED URI : " + transferURI.toString());
			employeeService.updateTranferedAssetWithlabel(transfered, transferURI);
		} catch (NoResultException ex) {
			transfer = null;
		} catch (NotFoundException nf) {
			return Response.status(Response.Status.NOT_FOUND).entity("Employee don't exist").build();
		} catch (BadRequestException br) {
			return Response.status(Status.BAD_REQUEST).entity("Ensure Asset is transfered from the current custodian").build();
		} catch (ClientErrorException ce) {
			return Response.status(Status.CONFLICT).entity("Asset cannot be transfered!").build();
		} catch (WriterException | IOException ex) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (transfer == null) return Response.status(Response.Status.NOT_FOUND).entity("Asset was not found").build();
		return Response.created(transferURI).build();
	}
	
	@GET
	@Path("{id}/allocates")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeAllAssets(
			@PathParam("id") Long employeeId,
			@QueryParam("status") AllocationStatus filteredStatus) {
		List<Object> allocationsOrTransfers = employeeService.employeeAssets(filteredStatus, employeeId);
		if (allocationsOrTransfers.size() == 0)
			return Response.status(Status.NO_CONTENT).build();
		
		return Response.ok(allocationsOrTransfers).build();
	}
		
	@GET
	@Produces("image/png")
	@Path("{employeeId}/assets/{assetId}") 
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeQRPreview(
			@PathParam("employeeId") @NotNull Long employeeId,
			@PathParam("assetId") @NotNull Long assetId) {
		Allocation allocated = Allocation.assetForQRPreview(employeeId, assetId);
		Transfer transfered = Transfer.assetForQRPreview(employeeId, assetId);
		
		if (allocated == null && transfered == null) return Response.status(Status.NO_CONTENT).build();
		 return allocated == null 
				 ? Response.ok(transfered.asset.label.qrByteString).build() 
						 : Response.ok(allocated.asset.label.qrByteString).build();
	}
 
	@GET
	@Path("/{employeeId}/allocates/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response getQRDetails(
			@PathParam("employeeId") @NotNull Long employeeId,
			@PathParam("id") @NotNull Long id) {
		Allocation allocated = Allocation.qrPreviewDetails(employeeId, id);
		Transfer transfered = Transfer.qrPreviewDetails(employeeId, id);
		
		if (allocated == null && transfered == null) return Response.status(Status.NOT_FOUND).build();
		 return allocated == null 
				 ? Response.ok(transfered).build() 
						 : Response.ok(allocated).build();
	}
		
	@PUT
	@Path("/{id}")
	public Response updateEmployee(@PathParam("id") @NotNull Long empId, @Valid Employee employee) {
		if (!empId.equals(employee.id)) return Response.status(Response.Status.CONFLICT).entity(employee).build();
		else 	if (employee.department == null) return Response.status(Status.BAD_REQUEST).build();

		employee.address = null;
		try {
			employeeService.updateEmployee(employee, empId);
		} catch (NotFoundException nf) {
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
