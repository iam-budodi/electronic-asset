package com.japhet_sebastian.employee;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.io.Serializable;

public abstract class AbstractEmployeeType implements Serializable {
    protected static final Logger LOGGER = Logger.getLogger(EmployeeResource.class);

    public UriBuilder employeeUriBuilder(String id, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(id);
        LOGGER.info("New employee created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }
}
