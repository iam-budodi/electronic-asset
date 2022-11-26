package com.assets.management.assets.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity; 

@Entity
@Table(name = "items") 
public class Item extends PanacheEntity {

	@NotNull
	@Column(name = "item_name", length = 64, nullable = false)
	public String itemName;

	
	@NotNull
	@Column(name = "serial_number", length = 32, nullable = false)
	public String serialNumber; 
	
	@NotNull 
	public BigDecimal cost;
	
	@NotNull 
	@Min(1)
	@Column(name = "quantity", nullable = false)
	public Integer qty; 
	
	@NotNull
	@Column(name = "date_purchased")
	public LocalDate datePurchased; 

	@Column(length = 1000)
	public String description;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	public Category category;

	@ManyToOne(fetch = FetchType.LAZY) 
	public Supplier supplier; 
// 
//	@OneToOne(
//	        mappedBy = "item", cascade = CascadeType.ALL,
//	        fetch = FetchType.LAZY
//	)
//	public Label label;
}
