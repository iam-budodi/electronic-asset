package com.assets.management.assets.model.valueobject;

import com.assets.management.assets.model.entity.Asset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

public class EmployeeAsset {

    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public final Asset asset;

    public EmployeeAsset(Asset asset) {
        this.asset = asset;
    }
}
