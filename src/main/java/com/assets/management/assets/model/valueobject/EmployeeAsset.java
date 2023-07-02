package com.assets.management.assets.model.valueobject;

import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class EmployeeAsset {

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public final Asset asset;

    public final Employee employee;

    //    public final Set<AllocationStatus> status;
    public final AllocationStatus status;
//    @ProjectedFieldName("status") AllocationStatus

    public EmployeeAsset(Asset asset, Employee employee, AllocationStatus status) {
        this.asset = asset;
        this.employee = employee;
        this.status = status;
    }
}
