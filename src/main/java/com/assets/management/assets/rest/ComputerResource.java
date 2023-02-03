package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Item;
import com.assets.management.assets.model.entity.ItemAssignment;
import com.assets.management.assets.model.entity.Label;
import com.assets.management.assets.model.entity.Purchase;
import com.assets.management.assets.model.valueobject.QrContent;
import com.assets.management.assets.service.ComputerService;
import com.assets.management.assets.util.QrCodeClient;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;

@Path("/computers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
public class ComputerResource {
	
	@Inject
	Logger LOG;
	
	@Inject
	ComputerService computerService;
	
	@Inject
	QrCodeClient qrCodeClient;
	
	@GET
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response listAllComputers(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Computer> computers = Computer.find("SELECT DISTINCT c FROM Computer c "
				+ "LEFT JOIN FETCH c.category "
				+ "LEFT JOIN FETCH c.label "
				+ "LEFT JOIN FETCH c.purchase p "
				+ "LEFT JOIN FETCH p.supplier s "
				+ "LEFT JOIN FETCH s.address "
				+ "ORDER BY p.purchaseDate, c.brand, c.model")
				.page(pageIndex, pageSize).list();
		return Response.ok(computers).build();
	}
	
	@POST
	public Response createComputer(@Valid Computer computer, @Context UriInfo uriInfo) {
		LOG.info("CHECKING FOR PURCHASE OBJ: " + computer.purchase.id);
		if (Computer.checkSerialNumber(computer.serialNumber))
			return Response.status(Status.CONFLICT).entity("Duplicate is not allow!").build();
//		boolean exists =  Purchase.findByIdOptional(computer.purchase.id).isPresent();
		if (computer.purchase == null || computer.purchase.id == null)
			return Response.status(Status.BAD_REQUEST).entity("Invalid purchase details").build();
//		else if (!exists) 
//			return Response.status(Status.NOT_FOUND).entity("Make sure there's purchase record for the item").build();

		return Purchase.findByIdOptional(computer.purchase.id).map(
				purchase -> {
					Computer.persist(computer);
					URI computerUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(computer.id)).build();
					return Response.created(computerUri).build();
					}
				).orElseGet(() -> Response.status(Status.NOT_FOUND).entity("Purchase record dont exists").build());

//		Computer.persist(computer);
//		URI computerUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(computer.id)).build();
//		return Response.created(computerUri).build();
	}
//	
//	@POST
//	@Path("/{id}/allocates")
//	public Response assignItem(
//			@PathParam("id") @NotNull Long computerId, 
//			@QueryParam("employee") @NotNull Long employeeId, 
//			@Valid Allocation allocation,
//			@Context UriInfo uriInfo) {
//		
//		Optional<Allocation>  allocated =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
//				+ "LEFT JOIN FETCH a.employee e "
//				+ "LEFT JOIN FETCH a.asset c "
//				+ "WHERE c.id = :compId "
//				+ "AND e.id = :empId", 
//				Parameters.with("compId", computerId).and("empId", employeeId))
//				.firstResultOptional();
//
//		if (allocated.isPresent()) 
//			return Response.status(Status.CONFLICT).entity("Asset is already taken!").build();
//
//		Employee employee = Employee.findById(employeeId);
//		Computer computer =  Computer.findById(computerId);
//		
//		if (employee == null || computer == null) 
//			return Response.status(Response.Status.NOT_FOUND).entity("Employee/Computer don't exist").build();
//		
//		
//
//		QrContent qrContent = Computer.projectQrContents(computer.serialNumber);
//		computer.label.itemQrString = qrCodeClient.formatQrImgToString(qrContent);
//		allocation.employee = employee;
//		allocation.asset = computer;
//		
////		TODO: ENCRYPT THE  URL OF THE ALLOCATION DETAILS
////		By first allocate the computer
////		then create the qr code with the allocation URI 
////		persist the bytes of the qr code.
////		TODO: IN THE RELATED GET REQUEST RETURN THE QR CODE OF THE ENCRYPTED  URL OF THE ALLOCATION DETAILS
////		Label label = new Label();
////		label.itemQrString = "dummy".getBytes();
////		assignment.label = label;
////		assignment.item.status = Status.InUse;
////		assignment.item.transferCount = 0;
////		
////		assignment.label.itemAssignment = assignment;
////		assignment.label.id = assignment.id;
////		
////		ItemAssignment.persist(assignment);
////		assignment.label.itemQrString = qrCodeClient.formatQrImgToString(assignment.itemSerialNumber);
////		Panache.getEntityManager().merge(assignment);
////		
////		return assignment;
//		
//		
////		try {
////			assignment = assignmentService.assignItem(assignment, employeeId, itemId);
////		} catch (NotFoundException ex) {
////			return Response.status(Response.Status.NOT_FOUND).build();
////		}
//		
//		Allocation.persist(allocation);
//		URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)) .build();
//
//		return Response.created(uri).build();
//	}
	
	@GET
	@Path("/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response findComputerById(@PathParam("id") @NotNull Long computerId) {
		return Computer.find("SELECT DISTINCT c FROM Computer c "
				+ "LEFT JOIN FETCH c.category "
				+ "LEFT JOIN FETCH c.label "
				+ "LEFT JOIN FETCH c.purchase p " 
				+ "LEFT JOIN FETCH p.supplier s "
				+ "LEFT JOIN FETCH s.address "
				+ "WHERE c.id = :id", 
				Parameters.with("id", computerId))
				.firstResultOptional()
				.map(computer -> Response.ok(computer).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
	
	@PUT
	@Path("/{id}")
	public Response updateComputer(@PathParam("id") @NotNull Long computerId, @Valid Computer computer) {
		if (!computerId.equals(computer.id)) 
			return Response.status(Response.Status.CONFLICT).entity(computer).build();
		
		return Computer.findByIdOptional(computerId).map(
				exists -> {
					Panache.getEntityManager().merge(computer);
					return Response.status(Status.NO_CONTENT).build();
					}
				).orElseGet(() ->  Response.status(Status.NOT_FOUND).build());
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteComputer(@PathParam("id") @NotNull Long computerId) {
				return Computer.deleteById(computerId) 
						? Response.status(Status.NO_CONTENT).build() 
								: Response.status(Status.NOT_FOUND).build();
	}
}
