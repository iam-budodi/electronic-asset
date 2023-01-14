package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

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

import com.assets.management.assets.model.Supplier;
import com.assets.management.assets.service.SupplierService;

@Path("/suppliers")
public class SupplierResource {

	@Inject
	SupplierService supplierService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(
			@QueryParam("page") @DefaultValue("0") Integer index,
			@QueryParam("size") @DefaultValue("15") Integer size) {
		List<Supplier> suppliers = supplierService.listSuppliers(index, size);
		return Response.ok(suppliers).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findSupplier(@PathParam("id") @NotNull Long id) {
		return supplierService
				.findSupplier(id)
				.map(supplier -> Response.ok(supplier).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSupplier(@Valid Supplier supplier, @Context UriInfo uriInfo) {
		boolean isSupplier = Supplier.findByEmailAndPhone(supplier.email, supplier.phone).isPresent();
		if (supplier.address == null) return Response.status(Status.BAD_REQUEST).build();
		if (isSupplier)
			return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
				
		supplierService.createSupplier(supplier);
		URI supplierURI =  uriInfo.getAbsolutePathBuilder().path(Long.toString(supplier.id)).build();
		return 	Response.created(supplierURI).build();
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response countAll() {
		return Response.ok(Supplier.count()).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSupplier(@PathParam("id") @NotNull Long id, @Valid Supplier supplier) {
		if (!id.equals(supplier.id))
			return Response.status(Response.Status.CONFLICT).entity(supplier).build();

		try {
			supplierService.updateSupplier(supplier, id);
		} catch (NotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}
 
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") @NotNull Long suppId) {
		try {
			supplierService.deleteSupplier(suppId);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

//	@GET
//	@Path("/{id}/items")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response listAllSupplierItems(
//			@PathParam("id") Long supplierId,
//			@QueryParam("page") @DefaultValue("0") Integer pIndex,
//			@QueryParam("size") @DefaultValue("15") Integer pSize) {
//		List<Item> items = supplierService
//				.getItems(supplierId, pIndex, pSize);
//
//		return Response.ok(items).build();
//	}
}
