package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

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

import com.assets.management.assets.model.Purchase;
import com.assets.management.assets.model.Supplier;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;

@Path("/purchases")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
public class PurchaseResource {

	@GET
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response listAllPurchases(
			@QueryParam("page") @DefaultValue("0") Integer pageIndex,
			@QueryParam("size") @DefaultValue("15") Integer pageSize) {
		List<Purchase> purchases = Purchase.find("SELECT DISTINCT p FROM Purchase p "
				+ "LEFT JOIN FETCH p.supplier "
				+ "ORDER BY p.purchaseDate, p.purchaseQty DESC")
				.page(pageIndex, pageSize).list();
		return Response.ok(purchases).build();
	}
	
	@POST
	public Response makePurchase(@Valid Purchase purchase, @Context UriInfo uriInfo) {
		boolean isDuplicate =  Purchase.findByInvoice(purchase.invoiceNumber).isPresent();
		if (isDuplicate)  return Response.status(Status.CONFLICT).entity("Purchase record already exists!").build();
		else if (purchase.supplier != null) {
			boolean isSupplier = Supplier.findByIdOptional(purchase.supplier.id ).isPresent();
			if (!isSupplier) 
				return Response.status(Status.BAD_REQUEST).entity("Make sure there's supplier record for this purchase").build();	
		}
		
		Purchase.persist(purchase);
		URI purchaseURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(purchase.id)).build();
		return Response.created(purchaseURI).build();
	}
	
	@GET
	@Path("/{id: \\d+}")
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response findPurchaseById(@PathParam("id") @NotNull Long purchaseId) {
		return Purchase.find("SELECT DISTINCT p FROM Purchase p "
				+ "LEFT JOIN FETCH p.supplier "
				+ "WHERE p.id = :id ", 
				Parameters.with("id", purchaseId))
				.firstResultOptional()
				.map(purchase -> Response.ok(purchase).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
	
	@PUT
	@Path("/{id: \\d+}")
	public Response updatePurchase(@PathParam("id") @NotNull Long purchaseId, @Valid Purchase purchase) {
		if (!purchaseId.equals(purchase.id)) 
			return Response.status(Response.Status.CONFLICT).entity(purchase).build();
		
		return Purchase.findByIdOptional(purchaseId).map(
				exists -> {
					Panache.getEntityManager().merge(purchase);
					return Response.status(Status.NO_CONTENT).build();
					}
				).orElseGet(() ->  Response.status(Status.NOT_FOUND).build());
	}
	
	@DELETE
	@Path("/{id: \\d+}")
	public Response deletePurchase(@PathParam("id") @NotNull Long purchaseId) {
				return Purchase.deleteById(purchaseId) 
						? Response.status(Status.NO_CONTENT).build() 
								: Response.status(Status.NOT_FOUND).build();
	}
}
