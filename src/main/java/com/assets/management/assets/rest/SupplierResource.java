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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.service.SupplierService;

@Path("/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Supplier Endpoint", description = "This API allows to keep record of asset suppliers")
public class SupplierResource {

	@Inject
	SupplierService supplierService;

	@GET
	@Operation(summary = "Retrieves all available suppliers from the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class, type = SchemaType.ARRAY)),
				description = "Lists all the suppliers"),
		@APIResponse(responseCode = "204", description = "No supplier to display")
	})
	public Response listSuppliers(
			@Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer index,
			@Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer size) {
		List<Supplier> suppliers = supplierService.listSuppliers(index, size);
		if (suppliers.size() == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(suppliers).build();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Returns supplier for a given identifier")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)),
				description = "Returns a found supplier"),
		@APIResponse(responseCode = "400", description = "Invalid input"),                       
		@APIResponse(responseCode = "404", description = "Supplier is not found for a given identifier")
	})
	public Response findSupplier(@Parameter(description = "Supplier identifier", required = true) @PathParam("id") @NotNull Long id) {
		return supplierService.findSupplier(id)
				.map(supplier -> Response.ok(supplier).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@POST
	@Operation(summary = "Creates a valid supplier and stores it into the database")
	@APIResponses({
		@APIResponse(
				responseCode = "201", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
				description = "URI of the created supplier"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(
				responseCode = "409", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
				description = "Supplier duplications is not allowed")
	})
	public Response createSupplier(
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class))) 
			@Valid Supplier supplier, @Context UriInfo uriInfo) {
		boolean isSupplier = Supplier.findByEmailAndPhone(supplier.email, supplier.phone).isPresent();
		if (supplier.address == null) return Response.status(Status.BAD_REQUEST).build();
		if (isSupplier) return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
				
		supplierService.createSupplier(supplier);
		URI supplierURI =  uriInfo.getAbsolutePathBuilder().path(Long.toString(supplier.id)).build();
		return 	Response.created(supplierURI).build();
	}

	@GET
	@Path("/count")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Counts all suppliers available in the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Long.class)),
				description = "Number of all suppliers available"),
		@APIResponse(responseCode = "204", description = "No supplier available in the database")
	})
	public Response countAll() {
		Long nbSuppliers = Supplier.count();
		if (nbSuppliers == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(nbSuppliers).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Updates an existing supplier")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Supplier has been successfully updated"),
		@APIResponse(responseCode = "404", description = "Supplier to be updated does not exist in the database"),
		@APIResponse(responseCode = "415", description = "Format is not JSON"),
		@APIResponse(
				responseCode = "409", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class)),
				description = "Supplier payload is not the same as an entity object that needed to be updated")
	})
	public Response updateSupplier(
			@Parameter(description = "Supplier identifier", required = true) @PathParam("id") @NotNull Long supplierId, 
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Supplier.class))) 
			@Valid Supplier supplier) {
		if (!supplierId.equals(supplier.id)) return Response.status(Response.Status.CONFLICT).entity(supplier).build();

		try {
			supplierService.updateSupplier(supplier, supplierId);
		} catch (NotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}
 
	@DELETE
	@Path("/{id}")
	@Operation(summary = "Deletes an existing supplier")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Supplier has been successfully deleted"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(responseCode = "404", description = "Supplier to be deleted does not exist in the database"),
		@APIResponse(responseCode = "500", description = "Supplier not found")
	})
	public Response delete(@Parameter(description = "Supplier identifier", required = true)  @PathParam("id") @NotNull Long suppId) {
		try {
			supplierService.deleteSupplier(suppId);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}

}
