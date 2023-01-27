package com.assets.management.assets.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.assets.management.assets.model.valueobject.QrContent;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/codes")
@RegisterRestClient
@RegisterForReflection
public interface QrProxy {

	@POST
	@Path("/qr")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("image/png") 
	byte[] createQrString(QrContent content);
}
