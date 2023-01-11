package com.assets.management.assets.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(
		name = "purchases", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_invoice_number", 
						columnNames = { "invoice_number" })
				}
		)
public class Purchase extends PanacheEntity {

	@NotNull
	@PastOrPresent
	@Column(name = "purchase_date", nullable = false)
	public LocalDate purchaseDate;

	@Min(1)
	@NotNull
	@Column(name = "purchase_quantity", nullable = false)
	public Integer purchaseQty;

	@Min(1)
	@NotNull
	@Column(name = "purchase_price", nullable = false)
	public BigDecimal purchasePrice;

	@NotNull
	@Column(name = "invoice_number", nullable = false)
	public String invoiceNumber;

//	@MapsId
//	@JsonIgnore
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(
//			name = "supplier_fk", 
//			foreignKey = @ForeignKey(
//					name = "purchase_supplier_fk_constraint", 
//					foreignKeyDefinition = ""))
//	public Supplier supplier; 

	@Transient
	public Integer totalCost;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void calculateAgeAndRetireDate() {
		if (purchaseQty == null || purchasePrice == null) {
			totalCost = 0;
			return;
		}  
		
//		totalCost = purchasePrice.; // purchaseQty;
	}
}
