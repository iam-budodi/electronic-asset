package com.assets.management.assets.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.net.URI;

@Path("/generates")
@RegisterRestClient
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QRGeneratorServiceProxy {

    @POST
    @Path("/qrcode")
    byte[] generateQrString(URI collectionOrTransferURI);
}
