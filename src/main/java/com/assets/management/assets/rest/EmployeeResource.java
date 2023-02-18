package com.assets.management.assets.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
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
import com.assets.management.assets.model.entity.QRCode;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.SingleList;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.QRGenerator;
import com.google.zxing.WriterException;

import io.quarkus.hibernate.orm.panache.Panache;
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
	
	// TODO: GET IT FROM SERVICES CLASS 
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

//		Tuple allocationTransfer =  Panache.getEntityManager().createQuery("SELECT a AS allocation, t AS transfer FROM Allocation a "
//				+ "LEFT JOIN FETCH a.employee e "
//				+ "LEFT JOIN FETCH a.asset c "
//				+ "LEFT JOIN Transfer t ON c.id = t.asset.id "
//				+ "WHERE (c.id = :assetId AND e.id = :fromEmployeeId) "
//				+ "AND  e.id <> :toEmployeeId "
//				+ "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :allocationStatus) "
//				+ "OR  EXISTS ( "
//					+ "SELECT 1 FROM Transfer tr "
//					+ "WHERE tr.asset.id = :assetId "
//					+ "AND tr.fromEmployee.id <> :fromEmployeeId "
//					+ "AND tr.toEmployee.id <> :toEmployeeId "
//					+ "AND (:transferStatus  MEMBER OF t.status) "
//					+ "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :secondAllocationStatus)"
//				+ ") ", Tuple.class)
//		.setParameter("toEmployeeId", transfer.toEmployee.id)
//		.setParameter("fromEmployeeId", transfer.fromEmployee.id)
//		.setParameter("assetId", transfer.asset.id)
//		.setParameter("transferStatus", AllocationStatus.ALLOCATED)
//		.setParameter("secondAllocationStatus", Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERED))
//		.setParameter("allocationStatus", Arrays.asList(AllocationStatus.ALLOCATED))
//		.getSingleResult();
//
//		Allocation allocated = (Allocation) allocationTransfer.get("allocation");
//		Transfer transfered = (Transfer) allocationTransfer.get("transfer");
//		List<AllocationStatus> transferedStatus = Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERED);
//		
//		if (allocated == null)
//			return Response.status(Status.CONFLICT).entity("Asset cannot be transfered!").build();
//		
//		LOG.info("THEN TO THE USER ID : " + allocated.toString());
//		if (transfer.toEmployee == null) return Response.status(Response.Status.NOT_FOUND).entity("Employee don't exist").build();
//		
//		// test implementation 
//		Transfer newTransfer;
//		if (allocated.status.remove(AllocationStatus.ALLOCATED))  allocated.status.addAll(transferedStatus);
//		else if (transfered.status.remove(AllocationStatus.ALLOCATED)) {
//			if (!transfered.toEmployee.id.equals(transfer.fromEmployee.id)) 
//				return Response.status(Status.BAD_REQUEST).entity("Ensure Asset is transfered from the current custodian").build();
//			
//			transfered.status.addAll(transferedStatus); 
//			newTransfer = new Transfer();
//			newTransfer.status.add(AllocationStatus.ALLOCATED);
//			newTransfer.toEmployee = transfer.toEmployee;
//			newTransfer.asset = transfer.asset;
//			newTransfer.fromEmployee = transfer.fromEmployee;
//			newTransfer.transferRemark = transfer.transferRemark;
//
//			Transfer.persist(newTransfer);
//			URI transferURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(newTransfer.id)) .build();
//			allocated.asset.label.qrByteString = qrGenerator.generateQrString(transferURI);
//			return Response.created(transferURI).build();
//		}
//		
//		transfer.status.add(AllocationStatus.ALLOCATED);
//		Transfer.persist(transfer);
//		URI transferURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(transfer.id)) .build();
//		allocated.deallocationDate = Instant.now();
//		allocated.asset.label.qrByteString = qrGenerator.generateQrString(transferURI);
//		return Response.created(transferURI).build();
		
		URI transferURI = null;
		try {
			Transfer transfered = employeeService.transferAsset(transfer, fromEmployeeId);
			transferURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(transfer.id)) .build();
			employeeService.updateTranferedAssetWithlabel(transfered, transferURI);
//			employeeService.updateAssetWithlabel(transfer.asset, transferURI);
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
	@Path("{id}/allocations")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeAllAssets(
			@PathParam("id") Long employeeId,
			@QueryParam("status") AllocationStatus filteredStatus) {

		List<Allocation> allocations = Allocation.find(
		        "SELECT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH e.department "
				+ "LEFT JOIN FETCH e.address "
				+ "LEFT JOIN FETCH a.asset ast "
		        + "WHERE e.id = :employeeId AND (:status IS NULL OR :status MEMBER OF a.status)",
		        Parameters.with("employeeId", employeeId)
				.and("status", filteredStatus))
		        .list();

		List<Transfer> transfers = Transfer.find(
		        "SELECT t FROM Transfer t "
		        + "LEFT JOIN FETCH t.fromEmployee "
		        + "LEFT JOIN FETCH t.toEmployee "
		        + "LEFT JOIN FETCH t.asset "
		        + "WHERE t.toEmployee.id = :employeeId "
		        + "AND (:status IS NULL OR :status MEMBER OF t.status) ",
		        Parameters.with("employeeId", employeeId)
				.and("status", filteredStatus))
		        .list();

		List<Object> allocationsOrTransfers = new ArrayList<>();
		allocationsOrTransfers.addAll(allocations);
		allocationsOrTransfers.addAll(transfers);

		if (allocationsOrTransfers.size() == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(allocationsOrTransfers).build();

	}

	// TODO: implement the resource to redirect when the qr code is scanned and the address clicked
//	@GET
//	@Path("/{employeeId}/allocations/{allocationId}")
//	@Transactional(Transactional.TxType.SUPPORTS)
//	public Response getEmployeeAllocateAsset(
//			@PathParam("employeeId") @NotNull Long employeeId,
//			@PathParam("allocationId") @NotNull Long allocationId) {
//		Optional<Allocation>  allocation =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
//				+ "LEFT JOIN FETCH a.employee e "
//				+ "LEFT JOIN FETCH e.department "
//				+ "LEFT JOIN FETCH e.address "
//				+ "LEFT JOIN FETCH a.asset ast "
//				+ "LEFT JOIN FETCH ast.category "
//				+ "LEFT JOIN FETCH ast.label "
//				+ "LEFT JOIN FETCH ast.purchase p "
//				+ "LEFT JOIN FETCH p.supplier s "
//				+ "LEFT JOIN FETCH s.address "
//				+ "WHERE e.id = :employeeId "
//				+ "AND a.id = :allocationId ", 
//				Parameters.with("employeeId", employeeId)
//				.and("allocationId", allocationId))
//				.firstResultOptional();
//		
//		return allocation.map(foundAllocation -> Response.ok(foundAllocation).build())
//										.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
//	}
	
	// TODO: implement the resource to redirect when the qr code is scanned and the address clicked
	@GET
	@Path("/{employeeId}/transfers/{transferId}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response getTransferDetails(
			@PathParam("employeeId") @NotNull Long fromEmployeeId,
			@PathParam("transferId") @NotNull Long transferId) {
		Optional<Transfer>  transfer =  Transfer.find("SELECT DISTINCT t FROM Transfer t "
				+ "LEFT JOIN FETCH t.fromEmployee fe "
//				+ "LEFT JOIN FETCH fe.department "
//				+ "LEFT JOIN FETCH fe.address "
				+ "LEFT JOIN FETCH t.toEmployee te "
//				+ "LEFT JOIN FETCH te.department "
//				+ "LEFT JOIN FETCH te.address "
				+ "LEFT JOIN FETCH t.asset ast "
				+ "LEFT JOIN FETCH ast.category "
				+ "LEFT JOIN FETCH ast.label "
				+ "LEFT JOIN FETCH ast.purchase p "
				+ "LEFT JOIN FETCH p.supplier s "
				+ "LEFT JOIN FETCH s.address "
				+ "WHERE fe.id = :fromEmployeeId "
				+ "AND a.id = :allocationId ", 
				Parameters.with("employeeId", fromEmployeeId)
				.and("transferId", transferId))
				.firstResultOptional();
		
		return transfer.map(transferFound -> Response.ok(transferFound).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
		
	@GET
	@Path("{employeeId}/assets/{assetId}") 
	@Produces("image/png")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response employeeQRPreview(
			@PathParam("employeeId") @NotNull Long employeeId,
			@PathParam("assetId") @NotNull Long assetId) {
//		Optional<QRCode> label = Allocation.find("SELECT DISTINCT a.asset.label FROM Allocation a "
//				+ "WHERE a.employee.id = :employeeId "
//				+ "AND a.asset.id = :assetId "
//				+ "AND :status NOT MEMBER OF a.status ", //<> :status",  //  (:transferStatus  MEMBER OF t.status)
//				Parameters.with("employeeId", employeeId) 
//				.and("status", AllocationStatus.DEALLOCATED)
//				.and("assetId", assetId))
//				.firstResultOptional();
		
		
		Tuple allocationsOrTransfers = Panache.getEntityManager().createQuery(
				"SELECT a AS allocation, t AS transfer FROM Allocation a "
				+ "LEFT JOIN  Transfer t ON  a.employee.id = t.toEmployee.id " // if it fails try IN all to anf from employee id 
				+ "WHERE a.employee.id = :employeeId OR t.toEmployee.id = :employeeId "
				+ "AND a.asset.id = :assetId OR t.asset.id = :assetId "
				+ "AND (:status MEMBER OF a.status OR :status MEMBER OF t.status)", Tuple.class)
		        .setParameter("employeeId", employeeId)
		        .setParameter("status", AllocationStatus.ALLOCATED)
		        .setParameter("assetId", assetId)
		        .getSingleResult();
		

		Allocation allocated = (Allocation) allocationsOrTransfers.get("allocation");
		Transfer transfered = (Transfer) allocationsOrTransfers.get("transfer");
		
		 return allocated == null 
				 ? Response.ok(transfered.asset.label.qrByteString).build() 
						 : Response.ok(allocated.asset.label.qrByteString).build();
		 
//		return label.map(qrImage -> Response.ok(qrImage.qrByteString).build())
//							.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
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
