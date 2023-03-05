package com.assets.management.assets.client;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/generates")
@RegisterRestClient
@RegisterForReflection
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QrProxy {

	@POST
	@Path("/qrcode")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces("image/png") 
	byte[] generateQrString(URI collectionOrTransferURI);
}
