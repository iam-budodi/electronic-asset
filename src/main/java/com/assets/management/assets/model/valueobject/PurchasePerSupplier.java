package com.assets.management.assets.model.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

//@RegisterForReflection
public class PurchasePerSupplier {
//	
//	@NotNull
//	@Size(min = 2, max = 64)
//	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@JsonProperty("supplierName")
	public final String supplierName;
//	
//	@Min(1)
//	@NotNull
	@JsonProperty("purchaseCount")
	public final Long purchaseCount;
	
//	public PurchasePerSupplier() {super();}
	@JsonCreator
	public PurchasePerSupplier(@ProjectedFieldName("supplier.name") String supplierName, Long purchaseCount) {
		this.supplierName = supplierName;
		this.purchaseCount = purchaseCount;
	}
}
