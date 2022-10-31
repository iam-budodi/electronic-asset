package com.assets.management.assets.rest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.service.AssetService;

@Path("/assets")
public class AssetResource {

    @Inject
    Logger LOG;

    @Inject
    AssetService assetService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllAssets(
            @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @QueryParam("size") @DefaultValue("15") Integer pageSize
    ) {
        List<Asset> assets = assetService.getAllAssets(pageIndex, pageSize);
        return Response.ok(assets).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAsset(@PathParam("id") @NotNull Long id) {
        Asset asset;
        try {
            asset = assetService.findById(id);
        } catch (NotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(asset).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAssetPerVendor() {
        return Response.ok(assetService.countAssetPerVendor()).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAssetPerStatus() {
        return Response.ok(assetService.countAssetPerStatus()).build();
    }

    @GET
    @Path("/total")
    @Produces(MediaType.APPLICATION_JSON)
    public Response totalAssetsCount() {
        return Response.ok(assetService.countAllAssets()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAsset(
            @PathParam("id") @NotNull Long id,
            @Valid Asset asset
    ) {

        if (asset == null || id == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        if (!id.equals(asset.id))
            return Response.status(Response.Status.CONFLICT).entity(asset)
                    .build();

        try {
            assetService.updateAsset(asset, id);
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
            assetService.deleteById(id);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
