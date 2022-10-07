package com.assets.management.electronic.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.assets.management.electronic.model.QrContent;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterRestClient
@RegisterForReflection
public interface QrProxy {

	@POST
	@Path("/devices/qr")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("image/png") 
	byte[] CreateQrString(QrContent content);
}
