package com.japhet_sebastian.vo;

import com.japhet_sebastian.employee.EmployeeResource;
import com.japhet_sebastian.organization.boundary.CollegeResource;
import com.japhet_sebastian.organization.boundary.DepartmentResource;
import com.japhet_sebastian.procurement.purchase.PurchaseResource;
import com.japhet_sebastian.procurement.supplier.SupplierResource;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.io.Serializable;

public abstract class AbstractGenericType implements Serializable {
    protected static final Logger PURCHASE_LOGGER = Logger.getLogger(PurchaseResource.class);
    protected static final Logger EMPLOYEE_LOGGER = Logger.getLogger(EmployeeResource.class);
    protected static final Logger DEPARTMENT_LOGGER = Logger.getLogger(DepartmentResource.class);
    protected static final Logger SUPPLIER_LOGGER = Logger.getLogger(SupplierResource.class);

    public UriBuilder departmentUriBuilder(String departmentId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriBuilder(departmentId, uriInfo);
        DEPARTMENT_LOGGER.info("New Department created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }

    public UriBuilder employeeUriBuilder(String employeeId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriBuilder(employeeId, uriInfo);
        EMPLOYEE_LOGGER.info("New employee created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }

    public UriBuilder supplierUriBuilder(String supplierId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriBuilder(supplierId, uriInfo);
        SUPPLIER_LOGGER.info("New Supplier created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }

    public UriBuilder purchaseUriBuilder(String purchaseId, UriInfo uriInfo) {
        final UriBuilder uriBuilder = uriBuilder(purchaseId, uriInfo);
        PURCHASE_LOGGER.info("New Purchase created with URI " + uriBuilder.build().toString());
        return uriBuilder;
    }

    private UriBuilder uriBuilder(String employeeId, UriInfo uriInfo) {
        return uriInfo.getAbsolutePathBuilder().path(employeeId);
    }
}
