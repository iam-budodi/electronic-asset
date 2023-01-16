package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
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

import com.assets.management.assets.model.Computer;
import com.assets.management.assets.model.Purchase;
import com.assets.management.assets.service.ComputerService;

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
				+ "ORDER BY p.purchaseDate")
				.page(pageIndex, pageSize).list();
		return Response.ok(computers).build();
	}
	
	@POST
	public Response createComputer(@Valid Computer computer, @Context UriInfo uriInfo) {
		LOG.info("CHECKING FOR PURCHASE OBJ: " + computer.purchase.id);
		if (computer.purchase.id == null) return Response.status(Status.BAD_REQUEST).entity("Invalid purchase details").build();
		boolean exists =  Purchase.findByIdOptional(computer.purchase.id).isPresent();
		if (Computer.checkSerialNumber(computer.serialNumber))
			return Response.status(Status.CONFLICT).entity("Duplicate is not allow!").build();
		else if (!exists) 
			return Response.status(Status.NOT_FOUND).entity("Make sure there's purchase record for the item").build();
		
		Computer.persist(computer);
		URI computerUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(computer.id)).build();
		return Response.created(computerUri).build();
	}
	
	@GET
	@Path("/{id: \\d+}")
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
	@Path("/{id: \\d+}")
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
	@Path("/{id: \\d+}")
	public Response deleteComputer(@PathParam("id") @NotNull Long computerId) {
				return Computer.deleteById(computerId) 
						? Response.status(Status.NO_CONTENT).build() 
								: Response.status(Status.NOT_FOUND).build();
	}
}
