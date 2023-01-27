package com.assets.management.assets.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.assets.management.assets.model.entity.Category;
import com.assets.management.assets.service.CategoryService;


@Path("/categories")
public class CategoryResource {

	@Inject
	CategoryService categoryService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response listCategories(
			@QueryParam("name") String catName) {
		if (catName == null)
			return Response
					.ok(Category.findAllOrderByName())
					.build();

		return Category.findByName(catName).map(
		        category -> Response.ok(category).build()
		).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(Transactional.TxType.SUPPORTS)
	public Response findCategory(
			@PathParam("id") @NotNull Long catId) {
		return Category.findByIdOptional(catId).map(
		        category -> Response.ok(category).build()
		).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

	@GET
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countCategory() {
		return Response.ok(Category.count()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertCategory(
	        @Valid Category category, @Context UriInfo uriInfo) {
		category = categoryService.createCategory(category);
		URI catUri = uriInfo
				.getAbsolutePathBuilder()
				.path(Long.toString(category.id))
				.build();
		return Response.created(catUri).build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCategory(
	        @PathParam("id") @NotNull Long id, @Valid Category cat) {
		if (!id.equals(cat.id))
			return Response
					.status(Response.Status.CONFLICT)
					.entity(cat)
					.build();

		try {
			categoryService.updateCategory(cat, id);
		} catch (NotFoundException | NoResultException enf) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteCategory(
			@PathParam("id") @NotNull Long catId) {
		try {
			categoryService.deleteCategory(catId);
		} catch (EntityNotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
}
