package com.assets.management.assets.model.valueobject;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// registered for reflection in a separate file under resource folder
public class PurchasePerSupplier {

    @NotNull
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
    public final String supplierName;

    @Min(1)
    @NotNull
    public final Long purchaseCount;

    public PurchasePerSupplier(@ProjectedFieldName("supplier.name") String supplierName, Long purchaseCount) {
        this.supplierName = supplierName;
        this.purchaseCount = purchaseCount;
    }
}
