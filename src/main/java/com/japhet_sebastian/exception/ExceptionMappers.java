package com.japhet_sebastian.exception;

import jakarta.ws.rs.NotFoundException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<String> mapException(NotFoundException nfe) {
        return RestResponse.status(RestResponse.Status.NOT_FOUND);
    }
}
