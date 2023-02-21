package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.assets.management.assets.model.entity.Category;

import io.quarkus.hibernate.orm.panache.Panache;


@Path("/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@Tag(name="Category Endpoint", description = "This API allows to group and CRUD related assets")
public class CategoryResource {

	@GET
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Retrieves all asset categories from the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class, type = SchemaType.ARRAY)),
				description = "Lists all the categories"),
		@APIResponse(responseCode = "204", description = "No categories to display"),
		@APIResponse(responseCode = "404", description = "Categories is not found for a given name query")
	})
	public Response listCategories(@Parameter(description = "Category name query parameter", required = false)  @QueryParam("name") String catName) {
		List<Category> categories = Category.findAllOrderByName();
		if (categories.size() == 0) return Response.status(Status.NO_CONTENT).build();
		if (catName == null) return Response.ok(categories).build();

		return Category.findByName(catName)
				.map(category -> Response.ok(category).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/{id}")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Returns asset category for a given identifier")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)),
				description = "Returns a found category"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(responseCode = "404", description = "Categories is not found for a given identifier")
	})
	public Response findCategory(@Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId) {
		return Category.findByIdOptional(categoryId)
				.map(category -> Response.ok(category).build())
				.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Transactional(Transactional.TxType.SUPPORTS)
	@Operation(summary = "Counts all asset categories available in the database")
	@APIResponses({
		@APIResponse(
				responseCode = "200", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Long.class)),
				description = "Number of all categories available"),
		@APIResponse(responseCode = "204", description = "No categories available in the database")
	})
	public Response countCategory() {
		Long nbCategories = Category.count();
		if (nbCategories == 0) return Response.status(Status.NO_CONTENT).build();
		return Response.ok(nbCategories).build();
	}

	@POST
	@Operation(summary = "Creates a valid category and stores it into the database")
	@APIResponses({
		@APIResponse(
				responseCode = "201", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
				description = "URI of the created category"),
		@APIResponse(responseCode = "400", description = "Invalid input")
	})
	public Response insertCategory(
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class))) 
			@Valid Category category, @Context UriInfo uriInfo) {
		Category.persist(category);
		URI categoryURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(category.id)).build();
		return Response.created(categoryURI).build();
	}

	@PUT
	@Path("/{id}")
	@Operation(summary = "Updates an existing asset category")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Category has been successfully updated"),
		@APIResponse(responseCode = "404", description = "Category to be updated does not exist in the database"),
		@APIResponse(responseCode = "415", description = "Format is not JSON"),
		@APIResponse(
				responseCode = "409", 
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)),
				description = "Category payload is not the same as an entity object that needed to be updated")
	})
	public Response updateCategory(
			@Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId, 
			@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class))) 
			@Valid Category category) {
		if (!categoryId.equals(category.id))
			return Response.status(Response.Status.CONFLICT).entity(category).build();
		
		return Category.findByIdOptional(categoryId).map(found -> {
			Panache.getEntityManager().merge(category);
			return Response.status(Status.NO_CONTENT).build();
		}).orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Deletes an existing asset category")
	@APIResponses({
		@APIResponse(responseCode = "204", description = "Category has been successfully deleted"),
		@APIResponse(responseCode = "400", description = "Invalid input"),
		@APIResponse(responseCode = "404", description = "Category to be deleted does not exist in the database"),
		@APIResponse(responseCode = "500", description = "Category not found")
	})
	public Response deleteCategory(@Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId) {
		return Category.deleteById(categoryId) 
				?  Response.noContent().build() 
						: Response.status(Status.NOT_FOUND).build();
	}
}
