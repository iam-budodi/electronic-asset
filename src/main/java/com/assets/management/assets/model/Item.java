package com.assets.management.assets.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@DynamicInsert
@Table(name = "items")
public class Item extends PanacheEntity {

	@NotNull
	@Column(name = "item_name", length = 64, nullable = false)
	public String itemName;

	// Think about remove this property to assignment and transfer
	@NotNull
	@Column(name = "serial_number", length = 32, nullable = false)
	public String serialNumber;

	@NotNull
	public BigDecimal cost;

	@NotNull
	@Min(1)
	@Column(name = "quantity", nullable = false)
	public Integer qty;

	@ColumnDefault(value = "'New'")
//	@Generated(GenerationTime.INSERT) // substituted by @DynamicInsert
	@Enumerated(EnumType.STRING)
	@Column(name = "item_status", nullable = false)
	public Status status;

	@NotNull
	@Column(name = "date_purchased")
	public LocalDate datePurchased;

	@Column(length = 1000)
	public String description;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	public Category category;

	@ManyToOne( fetch = FetchType.LAZY)
	public Supplier supplier;

//  should be generated and mapped on the assignment and transfer table
//	@OneToOne(
//	        mappedBy = "item", 
//	        cascade = CascadeType.ALL,
//	        fetch = FetchType.LAZY
//	)
//	public Label label;
}
