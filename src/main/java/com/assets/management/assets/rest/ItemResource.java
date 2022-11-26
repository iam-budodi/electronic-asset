package com.assets.management.assets.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.service.ItemService;

@Path("/items")
public class ItemResource {

    @Inject
    Logger LOG;

    @Inject
    ItemService itemService;
    
	@POST
//	@Path("/{id}/assets")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAsset(
//	        @PathParam("id") Long vendorId,
	        @Valid Item asset,
	        @Context UriInfo uriInfo
	) { 
		try {
			asset = itemService.addItem(asset);
		} catch (NoResultException | NotFoundException nfe) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (NonUniqueResultException | IllegalArgumentException
		        | BadRequestException bre) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		URI        uri = uriInfo.getAbsolutePathBuilder().path(
		        Long.toString(asset.id)
		).build();

		return Response.created(uri).build();
	}
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllItems(
            @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @QueryParam("size") @DefaultValue("15") Integer pageSize
    ) {
    	LOG.info("Calling all items service...");
        List<Item> items = itemService.getAllItems(pageIndex, pageSize);
        return Response.ok(items).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findItem(@PathParam("id") @NotNull Long itemId) {
        Item item;
        try {
        	item = itemService.findById(itemId);
        } catch (NotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(item).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAssetPerVendor() {
        return Response.ok(itemService.countAssetPerVendor()).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAssetPerStatus() {
        return Response.ok(itemService.countAssetPerStatus()).build();
    }

    @GET
    @Path("/total")
    @Produces(MediaType.APPLICATION_JSON)
    public Response totalAssetsCount() {
        return Response.ok(itemService.countAllAssets()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAsset(
            @PathParam("id") @NotNull Long id,
            @Valid Item asset
    ) {

        if (asset == null || id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        if (!id.equals(asset.id))
            return Response.status(Response.Status.CONFLICT).entity(asset)
                    .build();

        try {
            itemService.updateAsset(asset, id);
        } catch (EntityNotFoundException | NoResultException enf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (NonUniqueResultException nur) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAsset(@PathParam("id") @NotNull Long id) {
        try {
            itemService.deleteById(id);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
