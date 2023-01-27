package com.assets.management.assets.model.valueobject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
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
