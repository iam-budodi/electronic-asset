package com.assets.management.assets.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

public class ErrorResponseMapper implements ExceptionMapper<Exception> {

    private static final List<MediaType> ERROR_MEDIA_TYPES = List.of(MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_JSON_TYPE);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Logger logger;

    @Inject
    Provider<HttpServerRequest> httpServerRequestProvider;

    @Override
    public Response toResponse(Exception exception) {
        Response errorResponse = mapExceptionToResponse(exception);
//        List<MediaType> acceptableMedia = Vertx.vertx()..extractAccepts(VertxUtil.extractRequestHeaders(httpServerRequestProvider.get()));
        return null;
    }

    private Response mapExceptionToResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            Response originalErrorResponse = ((WebApplicationException) exception).getResponse();
            return Response.fromResponse(originalErrorResponse)
                    .entity(exception.getMessage())
                    .build();
        } else if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(exception.getMessage()).build();
        } else {
            logger.fatalf(exception,
                    "Failed to process request to: {}",
                    httpServerRequestProvider.get().absoluteURI());

            return Response.serverError().entity("Internal Server Error").build();
        }
    }
}
