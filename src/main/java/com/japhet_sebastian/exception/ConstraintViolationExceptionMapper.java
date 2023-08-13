package com.japhet_sebastian.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException cve) {
        List<ErrorResponse.ErrorMessage> errorMessages = cve.getConstraintViolations().stream().map(
                        violation -> new ErrorResponse.ErrorMessage(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(errorMessages)).build();
    }
}
