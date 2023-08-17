package com.japhet_sebastian.organization.boundary;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

public class AbstractDepartmentType {
    protected static final Logger LOGGER = Logger.getLogger(CollegeResource.class);

    public UriBuilder departmentUriBuilder(String departmentId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(departmentId);
        LOGGER.info("New Department created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }
}
