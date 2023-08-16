package com.japhet_sebastian.organization.boundary;

import jakarta.ws.rs.DefaultValue;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Objects;

public class DepartmentPageRequest extends PageRequest {

    @RestQuery("prop")
    @DefaultValue("departmentName")
    @Parameter(description = "Sort order property")
    private String departmentSortProperty;

    @RestQuery("order")
    @DefaultValue("asc")
    @Parameter(description = "Sort order property direction")
    private String SortDirection;

    public String getDepartmentSortProperty() {
        return departmentSortProperty;
    }

    public void setDepartmentSortProperty(String departmentSortProperty) {
        this.departmentSortProperty = departmentSortProperty;
    }

    public String getSortDirection() {
        return SortDirection;
    }

    public void setSortDirection(String sortDirection) {
        SortDirection = sortDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentPageRequest that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getDepartmentSortProperty(), that.getDepartmentSortProperty()) &&
                Objects.equals(getSortDirection(), that.getSortDirection());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDepartmentSortProperty(), getSortDirection());
    }
}
