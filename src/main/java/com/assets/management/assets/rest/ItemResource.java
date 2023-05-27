package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Item;
import com.assets.management.assets.service.ItemService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/items")
public class ItemResource {

    @Inject
    Logger LOG;

    @Inject
    ItemService itemService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllItems(
            @QueryParam("page") @DefaultValue("0") Integer pIndex,
            @QueryParam("size") @DefaultValue("15") Integer pSize) {
        List<Item> items = itemService
                .getAllItems(pIndex, pSize);

        return Response.ok(items).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertItem(
            @Valid Item item, @Context UriInfo uriInfo) {
        try {
            item = itemService.addItem(item);
        } catch (NoResultException | NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (NonUniqueResultException | BadRequestException bre) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        URI uri = uriInfo
                .getAbsolutePathBuilder()
                .path(Long.toString(item.id))
                .build();

        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response findItem(@PathParam("id") @NotNull Long itemId) {
        return Item.findByIdOptional(itemId).map(
                item -> Response.ok(item).build()
        ).orElseGet(
                () -> Response
                        .status(Status.NOT_FOUND)
                        .build()
        );
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPerSupplier() {
        return Response
                .ok(itemService.countItemPerSupplier())
                .build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPerStatus() {
        return Response.ok(itemService.countItemPerStatus()).build();
    }

    @GET
    @Path("/total")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.SUPPORTS)
    public Response totalCount() {
        return Response.ok(Item.count()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateItem(
            @PathParam("id") @NotNull Long itemId, @Valid Item item) {
        if (!itemId.equals(item.id))
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(item).build();

        try {
            itemService.updateItem(item, itemId);
        } catch (NotFoundException | NoResultException enf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (NonUniqueResultException nur) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delItem(@PathParam("id") @NotNull Long itemId) {
        try {
            itemService.deleteById(itemId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
