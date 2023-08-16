package com.japhet_sebastian.organization.boundary;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.io.Serializable;

public abstract class AbstractCollegeType implements Serializable {
    protected static final Logger LOGGER = Logger.getLogger(CollegeResource.class);

    public UriBuilder collegeUriBuilder(String id, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(id);
        LOGGER.info("New College created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }
}
