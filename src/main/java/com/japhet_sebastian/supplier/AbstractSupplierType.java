package com.japhet_sebastian.supplier;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

public class AbstractSupplierType {
    protected static final Logger LOGGER = Logger.getLogger(SupplierResource.class);

    public UriBuilder supplierUriBuilder(String supplierId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(supplierId);
        LOGGER.info("New Supplier created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }
}
