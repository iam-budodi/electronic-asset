package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "item_assignment")
public class ItemAssignment extends PanacheEntity {

	@NotNull 
	@Column(name = "date_assigned", nullable = false)
	public LocalDate dateAssigned;

	@Column(length = 24)
	public String condition;

	@NotNull 
	@Min(1)
	@Column(name = "quantity", nullable = false)
	public Integer qty;
	

	@Column(length = 4000)
	public String remarks;
	

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
	

	@ManyToOne(fetch = FetchType.LAZY)
	public Employee employee;
}
